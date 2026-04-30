#!/usr/bin/env node

import { execFileSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';

function parseArgs(argv) {
  const args = {
    issue: null,
    base: 'dev',
    head: null,
    title: null,
    dryRun: false,
    syncFeature: null,
  };

  const tokens = argv.slice(2);
  while (tokens.length > 0) {
    const token = tokens.shift();
    switch (token) {
      case '--issue':
        args.issue = Number(tokens.shift());
        break;
      case '--base':
        args.base = tokens.shift() || 'dev';
        break;
      case '--head':
        args.head = tokens.shift() || null;
        break;
      case '--title':
        args.title = tokens.shift() || null;
        break;
      case '--dry-run':
        args.dryRun = true;
        break;
      case '--sync-feature':
        args.syncFeature = tokens.shift() || null;
        break;
      case '--help':
        printHelp();
        process.exit(0);
      default:
        throw new Error(`Unknown option: ${token}`);
    }
  }
  return args;
}

function printHelp() {
  console.log(`Usage:
  node scripts/create-pr-from-issue.mjs [options]

Options:
  --issue <number>   Issue number (default: infer from current branch feat/<n>-...)
  --base <branch>    Base branch (default: dev)
  --head <branch>    Head branch (default: current branch)
  --title "<text>"   PR title (default: issue title)
  --sync-feature <id>  Run spec->planning sync before PR creation
  --dry-run          Print gh command only
  --help             Show help
`);
}

function gitText(args) {
  return execFileSync('git', args, { encoding: 'utf8' }).trim();
}

function ghJson(args) {
  return JSON.parse(execFileSync('gh', args, { encoding: 'utf8' }));
}

function inferIssueFromBranch(branch) {
  const match = branch.match(/^feat\/(\d+)-/);
  if (!match) return null;
  return Number(match[1]);
}

function buildBodyPath(issueNumber) {
  return path.join('.codex', 'orchestrator', 'pr-bodies', `issue-${issueNumber}.md`);
}

function resolveBodyArgs(issueNumber, syncedFile) {
  const bodyFile = buildBodyPath(issueNumber);
  if (fs.existsSync(bodyFile)) {
    return ['--body-file', bodyFile];
  }
  const changedFileLine = syncedFile ? `- ${syncedFile}` : '- (자동 생성 PR body 파일이 없어 수동 보완 필요)';
  const fallbackBody = [
    '## 변경 내용',
    '- 이번 PR에서 바꾼 핵심 내용',
    '',
    '## 핸드오프: 변경 파일 목록',
    changedFileLine,
    '',
    '## 핸드오프: 검증 결과',
    '- (자동 생성 PR body 파일이 없어 수동 보완 필요)',
    '',
    '## 핸드오프: 남은 리스크',
    '- (자동 생성 PR body 파일이 없어 수동 보완 필요)',
    '',
    '## 관련 이슈',
    `- closes #${issueNumber}`,
    '',
  ].join('\n');
  return ['--body', fallbackBody];
}

function runSpecPlanningSync(featureId) {
  if (!featureId) return null;
  const syncCommand = ['scripts/sync-spec-to-planning.mjs', '--feature', featureId];
  execFileSync('node', syncCommand, { stdio: 'inherit' });
  return `docs/planning/exec-plans/active/${featureId}.md`;
}

function main() {
  const args = parseArgs(process.argv);
  const currentBranch = gitText(['branch', '--show-current']);
  const head = args.head || currentBranch;
  const issueNumber = args.issue || inferIssueFromBranch(head);

  if (!issueNumber || !Number.isFinite(issueNumber)) {
    throw new Error('Issue number is required. Use --issue <number> or branch feat/<n>-... pattern.');
  }

  const issue = ghJson(['issue', 'view', String(issueNumber), '--json', 'title']);
  const title = args.title || issue.title;
  const syncedFile = runSpecPlanningSync(args.syncFeature);
  const bodyArgs = resolveBodyArgs(issueNumber, syncedFile);

  const command = [
    'pr',
    'create',
    '--base',
    args.base,
    '--head',
    head,
    '--title',
    title,
    ...bodyArgs,
  ];

  if (args.dryRun) {
    if (syncedFile) {
      console.log(`synced planning file: ${syncedFile}`);
    }
    console.log(`gh ${command.map((part) => (part.includes(' ') ? `"${part}"` : part)).join(' ')}`);
    return;
  }

  const url = execFileSync('gh', command, { encoding: 'utf8' }).trim();
  console.log(url);
}

main();
