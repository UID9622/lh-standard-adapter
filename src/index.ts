/**
 * lh-standard-adapter — LongHun Standard Adapter v1.0.0
 * 
 * DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0
 * Author: LongHun Core · UID9622 · 龍芯北辰
 * License: CC BY-NC-SA 4.0
 * 
 * This adapter wraps JSON payloads with DNA traceability
 * and seven-factor behavioral audit metadata.
 */

export const VERSION = "1.0.0";
export const DNA = "#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0-4f7a3b1c";

export { DNAGenerator } from './dna-generator';
export { AuditWrapper } from './audit-wrapper';
export { Validator } from './validator';
export { LongHunAdapter } from './adapter';
export { wrap } from './adapter';
export { generateDna } from './dna-generator';
export { auditWrap } from './audit-wrapper';
export { quickValidate } from './validator';
