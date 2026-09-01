/**
 * lh-standard-adapter — JavaScript/TypeScript implementation
 * DNA traceability + Seven-factor audit wrapping
 */

const { generateDnaCode, getStemBranch, selectHexagram } = require("./lib/dna_generator");
const { wrapPayload, generateAuditSignature, classifyBehavior } = require("./lib/audit_wrapper");
const { validateDnaCode, validateAuditSignature, validateWrappedPayload } = require("./lib/validator");

class LongHunAdapter {
  constructor(options = {}) {
    this.uid = options.uid || "";
    this.device = options.device || "";
  }

  wrap(data, taskType = "default", persona = "default") {
    return wrapPayload(data, taskType, persona, {
      uid: this.uid,
      device: this.device,
    });
  }

  validate(wrapped) {
    return validateWrappedPayload(wrapped);
  }

  getSchemas() {
    return {
      dnaSchema: {
        type: "string",
        pattern: "#LongHun⚡️...-DNA-GENERATOR-v1.0.0-...",
        description: "v∞ DNA traceability code",
      },
      auditSchema: {
        type: "object",
        properties: {
          pattern: { type: "string" },
          signature: { type: "string" },
          factors: {
            type: "object",
            properties: {
              P: { type: "string" },
              F: { type: "string" },
              T: { type: "string" },
              E: { type: "string" },
              C: { type: "string" },
              R: { type: "string" },
              A: { type: "string" },
              X: { type: "string" },
              Y: { type: "string" },
              Z: { type: "string" },
            },
          },
        },
      },
    };
  }
}

module.exports = { LongHunAdapter, generateDnaCode, wrapPayload, validateWrappedPayload };
module.exports.default = LongHunAdapter;
