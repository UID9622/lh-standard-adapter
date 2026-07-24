/**
 * LongHun Standard Adapter v1.0.0
 *
 * DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0
 * Author: LongHun Core · UID9622 · 龍芯北辰
 * License: CC BY-NC-SA 4.0
 *
 * Open the standard. Guard the engine.
 *
 * This adapter is an open-source shell tool. It wraps JSON payloads
 * with DNA traceability and seven-factor behavioral audit metadata.
 * Core compiler, training scripts, and algorithm logic are protected
 * Chinese independent intellectual property.
 */

import { DNAGenerator, generateDna } from './dna_generator.js';
import { AuditWrapper, auditWrap } from './audit_wrapper.js';
import { Validator, quickValidate } from './validator.js';
import { DNA_SCHEMA, AUDIT_SCHEMA } from './schemas.js';

export const VERSION = "1.0.0";
export const AUTHOR = "LongHun Core · UID9622 · 龍芯北辰";
export const LICENSE = "CC BY-NC-SA 4.0";
export const DNA = "#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0-4f7a3b1c";

/**
 * LongHun Adapter — wrap JSON payloads with DNA traceability
 * and seven-factor behavioral audit metadata.
 */
export class LongHunAdapter {
    /**
     * @param {string} uid — owner identifier
     * @param {string} device — device fingerprint
     * @param {string} locale — timezone locale
     */
    constructor(uid = "9622", device = "HM-9622-001", locale = "Asia/Shanghai") {
        this.uid = uid;
        this.device = device;
        this.locale = locale;
        this._dnaGen = new DNAGenerator(uid, device, locale);
        this._audit = new AuditWrapper(uid);
        this._validator = new Validator();
    }

    /**
     * Wrap a payload with DNA traceability and audit metadata.
     *
     * @param {*} data — raw payload data
     * @param {string} taskType — task category (default: "default")
     * @param {string} persona — persona identifier (default: "P04")
     * @param {string} action — action tag (default: "WRAP")
     * @param {string|null} version — version tag (default: null → "V1.0")
     * @returns {{ dna: string, audit: object, payload: *, meta: object }}
     */
    wrap(data, taskType = "default", persona = "P04", action = "WRAP", version = null) {
        const dna = this._dnaGen.generate(taskType, action, version);
        const audit = this._audit.wrap(data, taskType, persona);
        const now = new Date();

        // ISO 8601 with +08:00 offset
        const ms = now.getTime() + 8 * 3600000;
        const d = new Date(ms);
        const pad = (n) => String(n).padStart(2, '0');
        const msPad = String(d.getUTCMilliseconds()).padStart(3, '0');
        const generatedAt = `${d.getUTCFullYear()}-${pad(d.getUTCMonth() + 1)}-${pad(d.getUTCDate())}` +
            `T${pad(d.getUTCHours())}:${pad(d.getUTCMinutes())}:${pad(d.getUTCSeconds())}` +
            `.${msPad}+08:00`;

        const meta = {
            adapter_version: VERSION,
            uid: this.uid,
            device: this.device,
            task_type: taskType,
            persona: persona,
            generated_at: generatedAt,
            format: "longhun-v∞",
        };

        return {
            dna,
            audit,
            payload: data,
            meta,
        };
    }

    /**
     * Validate a wrapped payload.
     *
     * @param {object} wrapped
     * @returns {{ valid: boolean, errors: string[], warnings: string[], summary: string }}
     */
    validate(wrapped) {
        return this._validator.validate(wrapped);
    }
}

// Re-export everything for convenience
export { DNAGenerator, generateDna } from './dna_generator.js';
export { AuditWrapper, auditWrap } from './audit_wrapper.js';
export { Validator, quickValidate } from './validator.js';
export { DNA_SCHEMA, AUDIT_SCHEMA } from './schemas.js';
