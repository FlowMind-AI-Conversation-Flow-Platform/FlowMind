#!/usr/bin/env node

import fs from 'node:fs/promises';
import path from 'node:path';

function parseArgs(argv) {
  const args = {
    feature: null,
    dryRun: false,
    outputDir: 'docs/planning/exec-plans/active',
  };

  const tokens = argv.slice(2);
  while (tokens.length > 0) {
    const token = tokens.shift();
    switch (token) {
      case '--feature':
        args.feature = tokens.shift() || null;
        break;
      case '--dry-run':
        args.dryRun = true;
        break;
      case '--output-dir':
        args.outputDir = tokens.shift() || args.outputDir;
        break;
      case '--help':
        printHelp();
        process.exit(0);
      default:
        throw new Error(`Unknown option: ${token}`);
    }
  }

  if (!args.feature) {
    throw new Error('Missing required option: --feature <id>');
  }
  return args;
}

function printHelp() {
  console.log(`Usage:
  node scripts/sync-spec-to-planning.mjs --feature <feature-id> [options]

Options:
  --dry-run                  Print planned output without writing files
  --output-dir <path>        Target directory (default: docs/planning/exec-plans/active)
  --help                     Show help
`);
}

function findSectionByPrefix(markdown, headingPrefix) {
  const lines = markdown.split(/\r?\n/);
  const startIndex = lines.findIndex((line) =>
    line.trim().toLowerCase().startsWith(`## ${headingPrefix.toLowerCase()}`)
  );
  if (startIndex < 0) return '';

  let endIndex = lines.length;
  for (let i = startIndex + 1; i < lines.length; i += 1) {
    if (lines[i].trim().startsWith('## ')) {
      endIndex = i;
      break;
    }
  }
  return lines.slice(startIndex + 1, endIndex).join('\n').trim();
}

function firstBulletLines(block, max = 6) {
  const lines = block
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter((line) => line.startsWith('- ') || /^\d+\.\s+/.test(line))
    .slice(0, max);
  return lines.length > 0 ? lines : ['- (spec에서 수동 보완 필요)'];
}

function collectPatternLines(block, pattern, max = 6) {
  const lines = block
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter((line) => pattern.test(line))
    .slice(0, max);
  return lines.length > 0 ? lines : ['- (spec에서 수동 보완 필요)'];
}

function storyLines(block, max = 6) {
  const lines = block
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter((line) => /^\d+\.\s+\*\*Given\*\*/.test(line))
    .slice(0, max);
  return lines.length > 0 ? lines : ['- (spec에서 수동 보완 필요)'];
}

function buildPlanMarkdown(featureId, specText) {
  const titleMatch = specText.match(/^#\s+Feature Specification:\s*(.+)$/m);
  const featureTitle = titleMatch ? titleMatch[1].trim() : featureId;
  const requirements = findSectionByPrefix(specText, 'Requirements');
  const criteria = findSectionByPrefix(specText, 'Success Criteria');
  const assumptions = findSectionByPrefix(specText, 'Assumptions');
  const stories = findSectionByPrefix(specText, 'User Scenarios & Testing');

  const goalBullets = collectPatternLines(requirements, /^- \*\*FR-\d+\*\*:/, 6);
  const successBullets = collectPatternLines(criteria, /^- \*\*SC-\d+\*\*:/, 6);
  const scopeBullets = storyLines(stories, 6);
  const assumptionBullets = firstBulletLines(assumptions, 4);

  return [
    `# ${featureTitle} Execution Plan`,
    '',
    `- source-spec: specs/${featureId}/spec.md`,
    `- generated-at: ${new Date().toISOString()}`,
    '',
    '## 제목',
    `- ${featureTitle}`,
    '',
    '## 목표',
    ...goalBullets,
    '',
    '## 범위',
    ...scopeBullets,
    '',
    '## 비범위',
    '- 외부 연계 시스템 실제 운영 전환',
    '- 인프라/배포 파이프라인 전체 재설계',
    '',
    '## 예상 변경 파일',
    '- backend/** (필요 기능 구현 시)',
    '- docs/planning/**',
    '- specs/**',
    '',
    '## 리스크',
    '- spec 요구사항 대비 구현 누락 가능성',
    '- 슬롯/의도 경계 케이스 오분류 가능성',
    '- LLM fallback 정책 과/소적용 가능성',
    '',
    '## 작업 단계',
    '1. spec 요구사항 매핑',
    '2. API/도메인 설계',
    '3. 구현',
    '4. 테스트/검증',
    '5. 문서 및 운영 반영',
    '',
    '## 검증 기준',
    ...successBullets,
    '',
    '## 가정',
    ...assumptionBullets,
    '',
    '## 진행 상태',
    '- `pending`',
  ].join('\n');
}

async function main() {
  const args = parseArgs(process.argv);
  const specPath = path.resolve('specs', args.feature, 'spec.md');
  const outputFileName = `${args.feature}.md`;
  const outputPath = path.resolve(args.outputDir, outputFileName);

  const specText = await fs.readFile(specPath, 'utf8');
  const planText = buildPlanMarkdown(args.feature, specText);

  if (args.dryRun) {
    console.log(`DRY-RUN: ${outputPath}`);
    console.log(planText);
    return;
  }

  await fs.mkdir(path.dirname(outputPath), { recursive: true });
  await fs.writeFile(outputPath, `${planText}\n`, 'utf8');
  console.log(`Created: ${outputPath}`);
}

main().catch((error) => {
  const message = error instanceof Error ? error.message : String(error);
  console.error(message);
  process.exit(1);
});
