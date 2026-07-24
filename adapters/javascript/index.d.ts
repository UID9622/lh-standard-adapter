/**
 * TypeScript declarations for lh-standard-adapter v1.0.0
 *
 * DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0
 * Author: LongHun Core · UID9622 · 龍芯北辰
 * License: CC BY-NC-SA 4.0
 */

declare module 'lh-standard-adapter' {
    export const VERSION: '1.0.0';
    export const AUTHOR: string;
    export const LICENSE: 'CC BY-NC-SA 4.0';
    export const DNA: string;

    /** LongHun Adapter — wrap JSON payloads with DNA traceability and seven-factor behavioral audit metadata. */
    export class LongHunAdapter {
        uid: string;
        device: string;
        locale: string;

        constructor(uid?: string, device?: string, locale?: string);

        wrap(
            data: any,
            taskType?: string,
            persona?: string,
            action?: string,
            version?: string | null
        ): WrappedPayload;

        validate(wrapped: WrappedPayload): ValidationResult;
    }

    export interface WrappedPayload {
        dna: string;
        audit: AuditRecord;
        payload: any;
        meta: MetaRecord;
    }

    export interface AuditRecord {
        audit_version: string;
        uid: string;
        persona: string;
        task_type: string;
        behavior_signature: BehaviorSignature;
        behavior_pattern: string;
        behavior_labels: string[];
        color: '🟢' | '🟡' | '🔴';
        timestamp: string;
        payload_hash: string;
    }

    export interface BehaviorSignature {
        P: 'HasPromise' | 'NoPromise';
        F: 'Fulfilled' | 'Unfulfilled' | 'Partial';
        T: number;
        E: 'Willing' | 'Perfunctory' | 'Resentful' | 'Numb';
        C: number;
        R: number;
        A: 'Self' | 'Partner' | 'Family' | 'Outsider' | 'Public';
        X: 'OverExplain' | 'Silent' | 'Genuine' | 'Indifferent';
        Y: 'Changed' | 'Resisted' | 'Indifferent' | 'NoResponse';
        Z: number;
    }

    export interface MetaRecord {
        adapter_version: string;
        uid: string;
        device: string;
        task_type: string;
        persona: string;
        generated_at: string;
        format: 'longhun-v∞';
    }

    export interface ValidationResult {
        valid: boolean;
        errors: string[];
        warnings: string[];
        summary: string;
    }

    // Re-exported sub-modules
    export { DNAGenerator, generateDna } from 'lh-standard-adapter/dna_generator';
    export { AuditWrapper, auditWrap } from 'lh-standard-adapter/audit_wrapper';
    export { Validator, quickValidate } from 'lh-standard-adapter/validator';
    export { DNA_SCHEMA, AUDIT_SCHEMA } from 'lh-standard-adapter/schemas';
}

declare module 'lh-standard-adapter/dna_generator' {
    export const TIAN_GAN: string[];
    export const DI_ZHI: string[];
    export const SHI_CHEN: string[];
    export const HEXAGRAMS: Hexagram[];
    export const TASK_HEXAGRAM_MAP: Record<string, string>;

    export interface Hexagram {
        symbol: string;
        en_name: string;
        cn_name: string;
        domain: string;
    }

    export interface StemBranch {
        year: string;
        month: string;
        day: string;
        shichen: string;
    }

    export class DNAGenerator {
        uid: string;
        device: string;
        locale: string;

        constructor(uid?: string, device?: string, locale?: string);

        generate(taskType?: string, action?: string, version?: string | null): string;
    }

    export function generateDna(taskType?: string, action?: string, version?: string | null): string;
}

declare module 'lh-standard-adapter/audit_wrapper' {
    export const P_VALUES: string[];
    export const F_VALUES: string[];
    export const E_VALUES: string[];
    export const A_VALUES: string[];
    export const X_VALUES: string[];
    export const Y_VALUES: string[];
    export const PATTERNS: Record<string, string>;
    export const LABEL_MAP: Record<string, Record<string, string>>;

    export class AuditWrapper {
        uid: string;

        constructor(uid?: string);

        wrap(payload: any, taskType?: string, persona?: string): AuditRecord;
    }

    export function auditWrap(payload: any, taskType?: string, persona?: string): AuditRecord;
}

declare module 'lh-standard-adapter/validator' {
    export class Validator {
        errors: string[];
        warnings: string[];

        constructor();

        validate(wrapped: object): ValidationResult;
    }

    export function quickValidate(wrapped: object): boolean;
}

declare module 'lh-standard-adapter/schemas' {
    export const DNA_SCHEMA: object;
    export const AUDIT_SCHEMA: object;
}
