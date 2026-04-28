import assert from 'node:assert/strict';

import {
  parseArgs,
  detectPriority,
  detectDomain,
  allocateBatches,
  isAllowedPath,
  slugify,
  extractMarkdownLinks,
  validatePhasesStatusText,
} from './orchestrator-manager.mjs';

function run(name, fn) {
  try {
    fn();
    console.log(`PASS: ${name}`);
  } catch (error) {
    console.error(`FAIL: ${name}`);
    throw error;
  }
}

run('parseArgs parses command and options', () => {
  const args = parseArgs([
    'node',
    'scripts/orchestrator-manager.mjs',
    'run',
    '--limit',
    '12',
    '--strict-gate',
    '--output',
    'docs/operations/out.md',
  ]);

  assert.equal(args.command, 'run');
  assert.equal(args.limit, 12);
  assert.equal(args.strictGate, true);
  assert.equal(args.output, 'docs/operations/out.md');
});

run('detectPriority prefers explicit labels', () => {
  const issue = {
    number: 10,
    title: 'minor docs update',
    labels: [{ name: 'p1' }],
  };
  assert.equal(detectPriority(issue), 'P1');
});

run('detectDomain classifies backend and docs domains', () => {
  const backendIssue = {
    number: 11,
    title: 'Implement dispatch api controller',
    labels: [],
  };
  const docsIssue = {
    number: 12,
    title: 'Update README and phase guide',
    labels: [],
  };

  assert.equal(detectDomain(backendIssue), 'backend');
  assert.equal(detectDomain(docsIssue), 'docs');
});

run('allocateBatches separates conflicting domains', () => {
  const items = [
    { number: 1, domain: 'backend', sharedRisk: false },
    { number: 2, domain: 'security', sharedRisk: false },
    { number: 3, domain: 'docs', sharedRisk: false },
  ];

  const batches = allocateBatches(items);
  assert.equal(batches.length >= 2, true);
});

run('isAllowedPath gates by domain policy', () => {
  assert.equal(isAllowedPath('backend', 'backend/src/main/java/App.java'), true);
  assert.equal(isAllowedPath('backend', 'README.md'), false);
  assert.equal(isAllowedPath('docs', 'docs/README.md'), true);
});

run('slugify normalizes title into branch-safe slug', () => {
  assert.equal(slugify('Add Dispatch API: v1!'), 'add-dispatch-api-v1');
  assert.equal(slugify('---'), 'slice');
});

run('extractMarkdownLinks finds markdown links', () => {
  const markdown = `
  - [docs](./docs/README.md)
  - [web](https://example.com)
  - [section](#phase)
  `;
  const links = extractMarkdownLinks(markdown);
  assert.deepEqual(links, ['./docs/README.md', 'https://example.com', '#phase']);
});

run('validatePhasesStatusText validates status values', () => {
  const valid = `
### Phase 1. Foundation
- 상태: \`completed\`

### Phase 2. Core
- 상태: \`pending\`
`;
  const invalid = `
### Phase 1. Foundation
- 상태: \`done\`
`;
  assert.equal(validatePhasesStatusText(valid).ok, true);
  assert.equal(validatePhasesStatusText(invalid).ok, false);
});

console.log('All orchestrator-manager checks passed.');
