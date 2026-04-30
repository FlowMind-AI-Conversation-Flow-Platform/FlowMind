#!/usr/bin/env node

import fs from 'node:fs/promises';
import path from 'node:path';
import { pathToFileURL } from 'node:url';
import { execFileSync, execSync } from 'node:child_process';

const DEFAULT_BASE_BRANCH = 'dev';
const DEFAULT_WORKTREE_ROOT = '.worktrees';
const DEFAULT_LIMIT = 30;
const DEFAULT_STATE_FILE = '.codex/orchestrator/state.json';
const DEFAULT_RUN_ARCHIVE_ROOT = 'docs/operations/orchestrator-runs';
const DEFAULT_PR_BODY_DIR = '.codex/orchestrator/pr-bodies';

const AGENT_BY_DOMAIN = {
  backend: 'backend-builder',
  docs: 'docs-keeper',
  quality: 'qa-reviewer',
  planning: 'orchestrator-lead',
  security: 'qa-reviewer',
  unknown: 'orchestrator-lead',
};

const PRIORITY_BY_LABEL = {
  p0: 'P0',
  p1: 'P1',
  p2: 'P2',
  p3: 'P3',
};

const PRIORITY_RANK = {
  P0: 0,
  P1: 1,
  P2: 2,
  P3: 3,
};

export function parseArgs(argv) {
  const args = {
    command: 'plan',
    limit: DEFAULT_LIMIT,
    state: 'open',
    base: DEFAULT_BASE_BRANCH,
    worktreeRoot: DEFAULT_WORKTREE_ROOT,
    execute: false,
    comment: false,
    createWorktrees: false,
    output: null,
    stateFile: DEFAULT_STATE_FILE,
    maxRetries: 2,
    strictGate: false,
    workerCommand: null,
  };

  const tokens = argv.slice(2);
  if (tokens[0] && !tokens[0].startsWith('--')) {
    const cmd = tokens.shift();
    if (cmd !== 'plan' && cmd !== 'run') {
      throw new Error(`Unsupported command: ${cmd}`);
    }
    args.command = cmd;
  }

  while (tokens.length > 0) {
    const token = tokens.shift();
    switch (token) {
      case '--limit':
        args.limit = Number(tokens.shift() || DEFAULT_LIMIT);
        break;
      case '--state':
        args.state = tokens.shift() || 'open';
        break;
      case '--base':
        args.base = tokens.shift() || DEFAULT_BASE_BRANCH;
        break;
      case '--worktree-root':
        args.worktreeRoot = tokens.shift() || DEFAULT_WORKTREE_ROOT;
        break;
      case '--output':
        args.output = tokens.shift() || null;
        break;
      case '--execute':
        args.execute = true;
        break;
      case '--strict-gate':
        args.strictGate = true;
        break;
      case '--comment':
        args.comment = true;
        break;
      case '--create-worktrees':
        args.createWorktrees = true;
        break;
      case '--state-file':
        args.stateFile = tokens.shift() || DEFAULT_STATE_FILE;
        break;
      case '--max-retries':
        args.maxRetries = Number(tokens.shift() || 2);
        break;
      case '--worker-command':
        args.workerCommand = tokens.shift() || null;
        break;
      case '--help':
        printHelp();
        process.exit(0);
      default:
        throw new Error(`Unknown option: ${token}`);
    }
  }

  if (!Number.isFinite(args.limit) || args.limit < 1) {
    throw new Error(`Invalid --limit value: ${args.limit}`);
  }
  if (!Number.isFinite(args.maxRetries) || args.maxRetries < 0) {
    throw new Error(`Invalid --max-retries value: ${args.maxRetries}`);
  }

  return args;
}

export function printHelp() {
  console.log(`Usage:
  node scripts/orchestrator-manager.mjs <plan|run> [options]

Options:
  --state <open|closed|all>      Issue state (default: open)
  --limit <number>               Max issues to scan (default: 30)
  --base <branch>                Base branch for worktrees (default: dev)
  --worktree-root <path>         Worktree root path (default: .worktrees)
  --output <path>                Write markdown plan/run file
  --execute                      Apply actions (without this, dry-run)
  --strict-gate                  Enforce scope + mandatory verification gate
  --create-worktrees             Create worktree+branch per issue (requires --execute)
  --comment                      Post assignment comment (requires --execute)
  --state-file <path>            State JSON (default: .codex/orchestrator/state.json)
  --max-retries <n>              Max retries before skip (default: 2)
  --worker-command <template>    Worker command template placeholders:
                                 {issue} {branch} {worktree} {agent} {domain} {priority}
  --help                         Show help

Examples:
  node scripts/orchestrator-manager.mjs plan --output docs/operations/orchestrator-dispatch-plan.md
  node scripts/orchestrator-manager.mjs run --execute --comment --strict-gate
`);
}

function ghJson(args) {
  const stdout = execFileSync('gh', args, { encoding: 'utf8' });
  return JSON.parse(stdout);
}

function gitText(args) {
  return execFileSync('git', args, { encoding: 'utf8' }).trim();
}

function runCommand(command, cwd) {
  try {
    const output = execSync(command, {
      cwd,
      encoding: 'utf8',
      stdio: 'pipe',
      shell: true,
    });
    return { ok: true, output };
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    return { ok: false, output: message };
  }
}

export function detectPriority(issue) {
  const labels = (issue.labels || []).map((label) => (label.name || '').toLowerCase());
  for (const label of labels) {
    if (PRIORITY_BY_LABEL[label]) {
      return PRIORITY_BY_LABEL[label];
    }
  }
  const text = `${issue.title} ${issue.body || ''}`.toLowerCase();
  if (/(security|auth|vulnerability|data loss|prod down|outage|incident)/.test(text)) return 'P0';
  if (/(broken|bug|fail|error|critical|blocker)/.test(text)) return 'P1';
  if (/(improve|ux|performance|optimi[sz]e|enhance)/.test(text)) return 'P2';
  return 'P3';
}

export function detectDomain(issue) {
  const labels = (issue.labels || []).map((label) => (label.name || '').toLowerCase());
  const text = `${issue.title} ${issue.body || ''}`.toLowerCase();
  const joined = `${labels.join(' ')} ${text}`;

  if (/(security|auth|jwt|cors|header|audit)/.test(joined)) return 'security';
  if (/(backend|api|controller|service|repository|spring|dispatch|intent|slot|scenario)/.test(joined)) return 'backend';
  if (/(docs|documentation|readme|guide|phase|plan)/.test(joined)) return 'docs';
  if (/(quality|test|qa|verification|reliability)/.test(joined)) return 'quality';
  if (/(planning|roadmap|execution plan)/.test(joined)) return 'planning';
  return 'unknown';
}

function hasSharedFileRisk(issue) {
  const text = `${issue.title} ${issue.body || ''}`.toLowerCase();
  return /(build\.gradle|application\.yml|shared dto|global config|security config|env)/.test(text);
}

function isConflict(a, b) {
  if (a.sharedRisk || b.sharedRisk) return true;
  if (a.domain === 'unknown' || b.domain === 'unknown') return true;
  if (a.domain === b.domain) return true;
  const riskyPair = new Set(['backend:security', 'security:backend', 'backend:quality', 'quality:backend']);
  return riskyPair.has(`${a.domain}:${b.domain}`);
}

export function allocateBatches(items) {
  const batches = [];
  for (const item of items) {
    let assigned = false;
    for (const batch of batches) {
      if (!batch.some((existing) => isConflict(existing, item))) {
        batch.push(item);
        assigned = true;
        break;
      }
    }
    if (!assigned) {
      batches.push([item]);
    }
  }
  return batches;
}

export function slugify(value) {
  return value
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '')
    .slice(0, 36) || 'slice';
}

function nowParts(now = new Date()) {
  const pad = (v) => String(v).padStart(2, '0');
  return {
    date: `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`,
    time: `${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`,
  };
}

async function writeArchive(kind, markdown, root = DEFAULT_RUN_ARCHIVE_ROOT) {
  const parts = nowParts();
  const archiveDir = path.join(root, parts.date);
  const archivePath = path.join(archiveDir, `${kind}-${parts.time}.md`);
  await fs.mkdir(archiveDir, { recursive: true });
  await fs.writeFile(archivePath, `${markdown}\n`, 'utf8');
  return archivePath;
}

export function extractMarkdownLinks(markdown) {
  const links = [];
  const regex = /\[[^\]]+\]\(([^)]+)\)/g;
  let match = regex.exec(markdown);
  while (match !== null) {
    links.push(match[1].trim());
    match = regex.exec(markdown);
  }
  return links;
}

function normalizeLinkTarget(target) {
  return target.split('#')[0].split('?')[0].trim();
}

function shouldSkipLinkTarget(target) {
  if (!target) return true;
  if (target.startsWith('#')) return true;
  if (/^(https?:|mailto:|tel:)/i.test(target)) return true;
  if (/^\/[A-Za-z]:\//.test(target)) return true;
  if (/^[A-Za-z]:[\\/]/.test(target)) return true;
  return false;
}

async function fileExists(targetPath) {
  try {
    await fs.access(targetPath);
    return true;
  } catch {
    return false;
  }
}

async function collectBrokenLinksInFile(fileAbsPath, repoRootAbsPath) {
  const broken = [];
  const markdown = await fs.readFile(fileAbsPath, 'utf8');
  const targets = extractMarkdownLinks(markdown);

  for (const rawTarget of targets) {
    const target = normalizeLinkTarget(rawTarget);
    if (shouldSkipLinkTarget(target)) continue;

    let resolvedPath;
    if (target.startsWith('/')) {
      resolvedPath = path.resolve(repoRootAbsPath, `.${target}`);
    } else {
      resolvedPath = path.resolve(path.dirname(fileAbsPath), target);
    }

    const exists = await fileExists(resolvedPath);
    if (!exists) {
      broken.push(`${path.relative(repoRootAbsPath, fileAbsPath)} -> ${rawTarget}`);
    }
  }

  return broken;
}

export function validatePhasesStatusText(markdown) {
  const allowed = new Set(['pending', 'in_progress', 'completed']);
  const lines = markdown.split(/\r?\n/);
  const errors = [];

  for (let i = 0; i < lines.length; i += 1) {
    const line = lines[i];
    if (!/^###\s+Phase\s+\d+\./.test(line)) continue;

    let foundStatus = false;
    for (let j = i + 1; j < lines.length; j += 1) {
      const candidate = lines[j];
      if (/^###\s+Phase\s+\d+\./.test(candidate)) break;
      const statusMatch = candidate.match(/^\s*-\s*상태:\s*`([^`]+)`/);
      if (statusMatch) {
        foundStatus = true;
        if (!allowed.has(statusMatch[1])) {
          errors.push(`invalid status "${statusMatch[1]}" near line ${j + 1}`);
        }
      }
    }

    if (!foundStatus) {
      errors.push(`missing status for phase section near line ${i + 1}`);
    }
  }

  return { ok: errors.length === 0, errors };
}

const ALLOWED_PHASE_STATUS = new Set(['pending', 'in_progress', 'completed']);
const ALLOWED_ORCHESTRATOR_STATUS = new Set(['planned', 'running', 'dispatched', 'failed', 'done']);

export function validatePlanningStatusMarkers(markdown) {
  const lines = markdown.split(/\r?\n/);
  const errors = [];

  for (let i = 0; i < lines.length; i += 1) {
    const match = lines[i].match(/상태:\s*`([^`]+)`/);
    if (!match) continue;
    if (!ALLOWED_PHASE_STATUS.has(match[1])) {
      errors.push(`invalid planning status "${match[1]}" near line ${i + 1}`);
    }
  }

  return { ok: errors.length === 0, errors };
}

export function validateOrchestratorAutomationStatusText(markdown) {
  const lines = markdown.split(/\r?\n/);
  const statusLine = lines.find((line) => line.includes('상태값:'));
  if (!statusLine) {
    return { ok: false, errors: ['missing orchestrator 상태값 line'] };
  }

  const values = Array.from(statusLine.matchAll(/`([^`]+)`/g)).map((entry) => entry[1].trim());
  const errors = [];

  for (const value of values) {
    if (!ALLOWED_ORCHESTRATOR_STATUS.has(value)) {
      errors.push(`invalid orchestrator status "${value}"`);
    }
  }
  for (const required of ALLOWED_ORCHESTRATOR_STATUS) {
    if (!values.includes(required)) {
      errors.push(`missing orchestrator status "${required}"`);
    }
  }

  return { ok: errors.length === 0, errors };
}

export function parseSourceSpecFromPlan(markdown) {
  const match = markdown.match(/^\s*-\s*source-spec:\s*(.+)\s*$/m);
  return match ? match[1].trim() : null;
}

export function validateSpecPlanningSyncPair(specPath, planPath, planMarkdown) {
  const errors = [];
  const sourceSpec = parseSourceSpecFromPlan(planMarkdown);
  if (!sourceSpec) {
    errors.push(`missing source-spec meta in ${planPath}`);
    return { ok: false, errors };
  }

  const normalizedSource = sourceSpec.replaceAll('\\', '/');
  const normalizedSpecPath = specPath.replaceAll('\\', '/');
  if (normalizedSource !== normalizedSpecPath) {
    errors.push(`source-spec mismatch in ${planPath}: expected ${normalizedSpecPath}, got ${normalizedSource}`);
  }

  const specFeature = normalizedSpecPath.match(/^specs\/([^/]+)\/spec\.md$/)?.[1];
  const planFeature = planPath.match(/^docs\/planning\/exec-plans\/active\/(.+)\.md$/)?.[1];
  if (!specFeature || !planFeature || specFeature !== planFeature) {
    errors.push(`feature id mismatch between ${specPath} and ${planPath}`);
  }

  return { ok: errors.length === 0, errors };
}

async function validateSpecPlanningSync(worktreeAbsPath, changedFiles) {
  const failures = [];
  const changedSpecs = changedFiles.filter((file) => /^specs\/[^/]+\/spec\.md$/.test(file));
  const changedPlans = changedFiles.filter((file) => /^docs\/planning\/exec-plans\/active\/.+\.md$/.test(file));

  const planByFeature = new Map();
  for (const planPath of changedPlans) {
    const feature = planPath.match(/^docs\/planning\/exec-plans\/active\/(.+)\.md$/)?.[1];
    if (feature) planByFeature.set(feature, planPath);
  }

  for (const specPath of changedSpecs) {
    const feature = specPath.match(/^specs\/([^/]+)\/spec\.md$/)?.[1];
    if (!feature) continue;
    const pairedPlan = planByFeature.get(feature);
    if (!pairedPlan) {
      failures.push(`spec-sync: missing paired planning file for ${specPath}`);
      continue;
    }
    const planAbsPath = path.resolve(worktreeAbsPath, pairedPlan);
    const exists = await fileExists(planAbsPath);
    if (!exists) {
      failures.push(`spec-sync: missing file ${pairedPlan}`);
      continue;
    }
    const planMarkdown = await fs.readFile(planAbsPath, 'utf8');
    const result = validateSpecPlanningSyncPair(specPath, pairedPlan, planMarkdown);
    failures.push(...result.errors.map((error) => `spec-sync: ${error}`));
  }

  for (const planPath of changedPlans) {
    const feature = planPath.match(/^docs\/planning\/exec-plans\/active\/(.+)\.md$/)?.[1];
    if (!feature) continue;
    const expectedSpec = `specs/${feature}/spec.md`;
    const specAbsPath = path.resolve(worktreeAbsPath, expectedSpec);
    const specExists = await fileExists(specAbsPath);
    if (!specExists) {
      failures.push(`spec-sync: missing source spec file ${expectedSpec}`);
      continue;
    }
    const planAbsPath = path.resolve(worktreeAbsPath, planPath);
    const planMarkdown = await fs.readFile(planAbsPath, 'utf8');
    const result = validateSpecPlanningSyncPair(expectedSpec, planPath, planMarkdown);
    failures.push(...result.errors.map((error) => `spec-sync: ${error}`));
  }

  return failures;
}

async function runDocsPlanningLint(worktreeAbsPath, changedFiles, domain) {
  const failures = [];
  const markdownFiles = changedFiles.filter(
    (file) => file.startsWith('docs/') && file.toLowerCase().endsWith('.md')
  );

  for (const file of markdownFiles) {
    const fileAbsPath = path.resolve(worktreeAbsPath, file);
    const exists = await fileExists(fileAbsPath);
    if (!exists) continue;
    const brokenLinks = await collectBrokenLinksInFile(fileAbsPath, worktreeAbsPath);
    failures.push(...brokenLinks.map((entry) => `broken-link: ${entry}`));

    const markdown = await fs.readFile(fileAbsPath, 'utf8');
    if (file.startsWith('docs/planning/')) {
      const planningStatus = validatePlanningStatusMarkers(markdown);
      failures.push(...planningStatus.errors.map((error) => `planning-status: ${file}: ${error}`));
    }
    if (file === 'docs/operations/orchestrator-manager-automation.md') {
      const orchestratorStatus = validateOrchestratorAutomationStatusText(markdown);
      failures.push(
        ...orchestratorStatus.errors.map((error) => `orchestrator-status: ${file}: ${error}`)
      );
    }
  }

  const shouldCheckPhases =
    domain === 'planning' ||
    changedFiles.includes('docs/planning/PHASES.md') ||
    changedFiles.some((file) => file.startsWith('docs/planning/'));

  if (shouldCheckPhases) {
    const phasesPath = path.resolve(worktreeAbsPath, 'docs/planning/PHASES.md');
    const exists = await fileExists(phasesPath);
    if (!exists) {
      failures.push('missing docs/planning/PHASES.md');
    } else {
      const phasesText = await fs.readFile(phasesPath, 'utf8');
      const validation = validatePhasesStatusText(phasesText);
      failures.push(...validation.errors.map((error) => `phase-status: ${error}`));
    }
  }

  const syncFailures = await validateSpecPlanningSync(worktreeAbsPath, changedFiles);
  failures.push(...syncFailures);

  return failures;
}

function planToMarkdown(batches, options) {
  const lines = [];
  lines.push('# Orchestrator Dispatch Plan');
  lines.push('');
  lines.push(`- generatedAt: ${new Date().toISOString()}`);
  lines.push(`- baseBranch: ${options.base}`);
  lines.push(`- totalIssues: ${batches.reduce((sum, b) => sum + b.length, 0)}`);
  lines.push(`- totalBatches: ${batches.length}`);
  lines.push('');

  batches.forEach((batch, index) => {
    lines.push(`## Batch ${index + 1} (${batch.length} issue${batch.length > 1 ? 's' : ''})`);
    lines.push('');
    lines.push('| Issue | Priority | Domain | Agent | Branch | Worktree |');
    lines.push('|---|---|---|---|---|---|');
    for (const item of batch) {
      lines.push(
        `| #${item.number} | ${item.priority} | ${item.domain} | ${item.agent} | \`${item.branch}\` | \`${item.worktree}\` |`
      );
    }
    lines.push('');
  });

  return lines.join('\n');
}

function stateToMarkdown(state) {
  const lines = [];
  lines.push('# Orchestrator Run State');
  lines.push('');
  lines.push(`- generatedAt: ${state.generatedAt}`);
  lines.push(`- baseBranch: ${state.baseBranch}`);
  lines.push('');
  lines.push('| Issue | Status | Retries | Agent | Branch | LastError |');
  lines.push('|---|---|---:|---|---|---|');
  for (const entry of Object.values(state.issues).sort((a, b) => a.number - b.number)) {
    lines.push(
      `| #${entry.number} | ${entry.status} | ${entry.retries} | ${entry.agent} | \`${entry.branch}\` | ${
        entry.lastError ? entry.lastError.replace(/\|/g, '\\|') : ''
      } |`
    );
  }
  lines.push('');
  return lines.join('\n');
}

function postAssignmentComment(item) {
  const body = [
    '## FlowMind 오케스트레이터 자동 배정',
    `- priority: \`${item.priority}\``,
    `- domain: \`${item.domain}\``,
    `- assigned-agent: \`${item.agent}\``,
    `- branch: \`${item.branch}\``,
    `- worktree: \`${item.worktree}\``,
    '',
    '### mandatory handoff',
    '- 변경 파일 목록',
    '- 검증 결과',
    '- 남은 리스크',
  ].join('\n');

  execFileSync('gh', ['issue', 'comment', String(item.number), '--body', body], { stdio: 'inherit' });
}

function ensureWorktree(item, baseBranch, worktreeRoot) {
  const existing = execFileSync('git', ['worktree', 'list', '--porcelain'], { encoding: 'utf8' });
  if (existing.includes(path.resolve(worktreeRoot, item.worktreeName))) return;
  execFileSync(
    'git',
    ['worktree', 'add', path.join(worktreeRoot, item.worktreeName), '-b', item.branch, `origin/${baseBranch}`],
    { stdio: 'inherit' }
  );
}

async function loadState(stateFile, baseBranch) {
  try {
    const raw = await fs.readFile(stateFile, 'utf8');
    return JSON.parse(raw);
  } catch {
    return {
      generatedAt: new Date().toISOString(),
      baseBranch,
      issues: {},
    };
  }
}

async function saveState(stateFile, state) {
  await fs.mkdir(path.dirname(stateFile), { recursive: true });
  await fs.writeFile(stateFile, JSON.stringify(state, null, 2), 'utf8');
}

function replaceTemplate(template, issue) {
  return template
    .replaceAll('{issue}', String(issue.number))
    .replaceAll('{branch}', issue.branch)
    .replaceAll('{worktree}', issue.worktree)
    .replaceAll('{agent}', issue.agent)
    .replaceAll('{domain}', issue.domain)
    .replaceAll('{priority}', issue.priority);
}

function listWorktreeChangedFiles(worktreeAbsPath) {
  const output = execFileSync('git', ['-C', worktreeAbsPath, 'status', '--porcelain'], { encoding: 'utf8' });
  return output
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)
    .map((line) => line.slice(3).trim().replaceAll('\\', '/'));
}

function listWorktreeChangedFilesSafe(worktreeAbsPath) {
  try {
    return listWorktreeChangedFiles(worktreeAbsPath);
  } catch {
    return [];
  }
}

export function isAllowedPath(domain, file) {
  const normalized = file.replaceAll('\\', '/');
  if (domain === 'backend' || domain === 'security' || domain === 'quality') {
    return normalized.startsWith('backend/') || normalized.startsWith('docs/') || normalized.startsWith('scripts/');
  }
  if (domain === 'docs' || domain === 'planning') {
    return normalized.startsWith('docs/') || normalized === 'README.md' || normalized === 'AGENT.md' || normalized === 'AGENTS.md';
  }
  return normalized.startsWith('docs/') || normalized.startsWith('scripts/');
}

function mandatoryVerification(domain, worktreeAbsPath) {
  const backendRoot = path.join(worktreeAbsPath, 'backend');
  if (domain === 'backend' || domain === 'security' || domain === 'quality') {
    return [{ name: 'backend-test', command: '.\\gradlew.bat test', cwd: backendRoot }];
  }
  return [];
}

async function runGate(issue) {
  const worktreeAbsPath = path.resolve(issue.worktree);
  const failures = [];
  const changedFiles = listWorktreeChangedFilesSafe(worktreeAbsPath);
  const outOfScope = changedFiles.filter((file) => !isAllowedPath(issue.domain, file));
  if (outOfScope.length > 0) {
    failures.push(`out-of-scope files: ${outOfScope.join(', ')}`);
  }

  const checks = mandatoryVerification(issue.domain, worktreeAbsPath);
  for (const check of checks) {
    const result = runCommand(check.command, check.cwd);
    if (!result.ok) failures.push(`${check.name} failed`);
  }

  if (issue.domain === 'docs' || issue.domain === 'planning' || changedFiles.some((file) => file.startsWith('docs/'))) {
    const lintFailures = await runDocsPlanningLint(worktreeAbsPath, changedFiles, issue.domain);
    failures.push(...lintFailures);
  }

  return { ok: failures.length === 0, failures, changedFiles };
}

async function writePrBodyDraft(issue, changedFiles, verificationLines, riskLines) {
  const prBodyPath = path.join(DEFAULT_PR_BODY_DIR, `issue-${issue.number}.md`);
  const changes = changedFiles.length > 0 ? changedFiles.map((file) => `- ${file}`) : ['- (no changes detected)'];
  const verification = verificationLines.length > 0 ? verificationLines.map((line) => `- ${line}`) : ['- (not provided)'];
  const risks = riskLines.length > 0 ? riskLines.map((line) => `- ${line}`) : ['- 없음'];

  const body = [
    '## 변경 내용',
    ...changes,
    '',
    '## 변경 이유',
    '- 이슈 요구사항 반영',
    '',
    '## 검토 포인트',
    `- domain: \`${issue.domain}\``,
    `- priority: \`${issue.priority}\``,
    '',
    '## 검증',
    ...verification,
    '',
    '## 핸드오프: 변경 파일 목록',
    ...changes,
    '',
    '## 핸드오프: 검증 결과',
    ...verification,
    '',
    '## 핸드오프: 남은 리스크',
    ...risks,
    '',
    '## 관련 이슈',
    `- closes #${issue.number}`,
    '',
  ].join('\n');

  await fs.mkdir(path.dirname(prBodyPath), { recursive: true });
  await fs.writeFile(prBodyPath, `${body}\n`, 'utf8');
  return prBodyPath;
}

export async function main() {
  const args = parseArgs(process.argv);
  const currentBranch = gitText(['branch', '--show-current']);
  const issues = ghJson([
    'issue',
    'list',
    '--state',
    args.state,
    '--limit',
    String(args.limit),
    '--json',
    'number,title,body,labels,url',
  ]);

  const normalized = issues
    .map((issue) => {
      const domain = detectDomain(issue);
      const priority = detectPriority(issue);
      const sharedRisk = hasSharedFileRisk(issue);
      const slug = slugify(issue.title);
      const branch = `feat/${issue.number}-${slug}`;
      const worktreeName = `feat-${issue.number}-${slug}`;
      return {
        ...issue,
        domain,
        priority,
        sharedRisk,
        agent: AGENT_BY_DOMAIN[domain] || AGENT_BY_DOMAIN.unknown,
        branch,
        worktreeName,
        worktree: path.join(args.worktreeRoot, worktreeName),
      };
    })
    .sort((a, b) => {
      const p = PRIORITY_RANK[a.priority] - PRIORITY_RANK[b.priority];
      return p !== 0 ? p : a.number - b.number;
    });

  const batches = allocateBatches(normalized);
  const markdown = planToMarkdown(batches, args);
  console.log(markdown);

  if (args.command === 'plan') {
    if (args.output) {
      await fs.mkdir(path.dirname(args.output), { recursive: true });
      await fs.writeFile(args.output, `${markdown}\n`, 'utf8');
      console.log(`Plan written: ${args.output}`);
    }
    const archivedPlanPath = await writeArchive('plan', markdown);
    console.log(`Plan archived: ${archivedPlanPath}`);
    return;
  }

  if (!args.execute) {
    const previewState = {
      generatedAt: new Date().toISOString(),
      baseBranch: args.base,
      issues: Object.fromEntries(
        normalized.map((item) => [
          String(item.number),
          {
            number: item.number,
            status: 'planned',
            retries: 0,
            updatedAt: new Date().toISOString(),
            agent: item.agent,
            branch: item.branch,
            worktree: item.worktree,
          },
        ])
      ),
    };

    const previewPath = args.output || 'docs/operations/orchestrator-run-state.md';
    await fs.mkdir(path.dirname(previewPath), { recursive: true });
    await fs.writeFile(previewPath, `${stateToMarkdown(previewState)}\n`, 'utf8');
    console.log(`Run preview written: ${previewPath}`);
    const previewArchivePath = await writeArchive('preview', stateToMarkdown(previewState));
    console.log(`Run preview archived: ${previewArchivePath}`);
    return;
  }

  const state = await loadState(args.stateFile, args.base);
  state.generatedAt = new Date().toISOString();
  state.baseBranch = args.base;

  for (const item of normalized) {
    const key = String(item.number);
    const previous = state.issues[key];
    if (previous?.status === 'done') continue;
    if ((previous?.retries || 0) >= args.maxRetries && previous?.status === 'failed') continue;

    state.issues[key] = {
      number: item.number,
      status: 'running',
      retries: previous?.retries || 0,
      updatedAt: new Date().toISOString(),
      agent: item.agent,
      branch: item.branch,
      worktree: item.worktree,
    };
    await saveState(args.stateFile, state);

    if (args.createWorktrees) ensureWorktree(item, args.base, args.worktreeRoot);
    if (args.comment) postAssignmentComment(item);

    if (args.workerCommand) {
      const command = replaceTemplate(args.workerCommand, item);
      const run = runCommand(command, process.cwd());
      if (!run.ok) {
        state.issues[key] = {
          ...state.issues[key],
          status: 'failed',
          retries: (state.issues[key].retries || 0) + 1,
          updatedAt: new Date().toISOString(),
          lastError: 'worker command failed',
        };
        await saveState(args.stateFile, state);
        continue;
      }
    } else {
      state.issues[key] = {
        ...state.issues[key],
        status: 'dispatched',
        updatedAt: new Date().toISOString(),
      };
      await saveState(args.stateFile, state);
    }

    let gateChangedFiles = listWorktreeChangedFilesSafe(path.resolve(item.worktree));
    const verificationLines = [];
    const riskLines = [];

    if (args.strictGate) {
      const gate = await runGate(item);
      gateChangedFiles = gate.changedFiles;
      verificationLines.push('strict-gate: enabled');
      if (!gate.ok) {
        verificationLines.push('strict-gate: failed');
        riskLines.push(...gate.failures);
        state.issues[key] = {
          ...state.issues[key],
          status: 'failed',
          retries: (state.issues[key].retries || 0) + 1,
          updatedAt: new Date().toISOString(),
          gateFailures: gate.failures,
          lastError: gate.failures.join('; '),
        };
        const prBodyPath = await writePrBodyDraft(item, gateChangedFiles, verificationLines, riskLines);
        state.issues[key].prBodyPath = prBodyPath;
        await saveState(args.stateFile, state);
        continue;
      }
      verificationLines.push('strict-gate: passed');
    } else {
      verificationLines.push('strict-gate: skipped');
    }

    state.issues[key] = {
      ...state.issues[key],
      status: 'done',
      updatedAt: new Date().toISOString(),
      lastError: undefined,
      gateFailures: undefined,
    };
    const prBodyPath = await writePrBodyDraft(item, gateChangedFiles, verificationLines, riskLines);
    state.issues[key].prBodyPath = prBodyPath;
    await saveState(args.stateFile, state);
  }

  const runSummaryPath = args.output || 'docs/operations/orchestrator-run-state.md';
  await fs.mkdir(path.dirname(runSummaryPath), { recursive: true });
  await fs.writeFile(runSummaryPath, `${stateToMarkdown(state)}\n`, 'utf8');
  const runArchivePath = await writeArchive('run', stateToMarkdown(state));

  console.log(`Execution complete. Current branch: ${currentBranch}`);
  console.log(`State written: ${args.stateFile}`);
  console.log(`Run summary: ${runSummaryPath}`);
  console.log(`Run archived: ${runArchivePath}`);
}

const isDirectRun = process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href;
if (isDirectRun) {
  main().catch((error) => {
    const message = error instanceof Error ? error.message : String(error);
    console.error(message);
    process.exit(1);
  });
}
