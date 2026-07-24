// CJS compatibility wrapper for lh-standard-adapter
//
// Re-exports the ESM module via createRequire for CommonJS consumers.

'use strict';

const { createRequire } = require('node:module');
const requireEsm = createRequire(import.meta.url);

// Dynamic import for CJS compatibility — we synchronously load the ESM module
// via createRequire + module patching.

const esmModule = requireEsm('./index.js');

module.exports = esmModule;
