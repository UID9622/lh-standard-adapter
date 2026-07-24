"""
Validator — DNA and audit format validation.

DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-VALIDATOR-v1.0.0
"""

import re
from datetime import datetime


# DNA v∞ validation regex
DNA_REGEX = re.compile(
    r"^#LongHun⚡️"
    r"([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)"  # Four pillars
    r"·([䷀-䷿][A-Za-z]+)"                                            # Hexagram
    r"-(.+)"                                                          # Body (module-action-version)
    r"-([a-f0-9]{8})$"                                                # Hash8
)

REQUIRED_TOP_KEYS = {"dna", "audit", "payload", "meta"}
REQUIRED_AUDIT_KEYS = {
    "audit_version", "uid", "behavior_signature",
    "behavior_pattern", "behavior_labels", "color"
}
REQUIRED_SIG_KEYS = {"P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"}
VALID_COLORS = {"🟢", "🟡", "🔴"}
VALID_PATTERNS = {
    "MODE-DefensiveDefaulter", "MODE-ExternalTrustSpender",
    "MODE-InternalDestroyer", "MODE-Fluctuating", "MODE-StableDisciplined"
}
VALID_P_VALUES = {"HasPromise", "NoPromise"}
VALID_F_VALUES = {"Fulfilled", "Unfulfilled", "Partial"}
VALID_E_VALUES = {"Willing", "Perfunctory", "Resentful", "Numb"}
VALID_A_VALUES = {"Self", "Partner", "Family", "Outsider", "Public"}
VALID_X_VALUES = {"OverExplain", "Silent", "Genuine", "Indifferent"}
VALID_Y_VALUES = {"Changed", "Resisted", "Indifferent", "NoResponse"}


class Validator:
    """Validate wrapped payloads for LongHun standard compliance."""

    def __init__(self):
        self.errors = []
        self.warnings = []

    def validate(self, wrapped: dict) -> dict:
        """
        Validate a wrapped payload.

        Returns: {"valid": bool, "errors": [...], "warnings": [...], "summary": str}
        """
        self.errors = []
        self.warnings = []

        if not isinstance(wrapped, dict) or not wrapped:
            self.errors.append("Input is not a non-empty dict")
            return self._result()

        # 1. Top-level keys
        missing = REQUIRED_TOP_KEYS - set(wrapped.keys())
        if missing:
            self.errors.append(f"Missing top-level keys: {missing}")

        # 2. DNA validation
        dna = wrapped.get("dna", "")
        if not dna:
            self.errors.append("DNA field is empty")
        else:
            match = DNA_REGEX.match(dna)
            if not match:
                self.errors.append(f"DNA does not match regex: {dna[:60]}...")
            else:
                hash8 = match.group(7)
                if len(hash8) != 8 or not all(c in "0123456789abcdef" for c in hash8):
                    self.errors.append(f"Invalid hash8: {hash8}")

            # 3. Audit validation
            audit = wrapped.get("audit", {})
            if not isinstance(audit, dict):
                self.errors.append("Audit is not a dict")
            else:
                self._validate_audit(audit)

                # 4. UID consistency check
                meta = wrapped.get("meta", {})
                if isinstance(meta, dict) and isinstance(audit, dict):
                    meta_uid = meta.get("uid", "")
                    audit_uid = audit.get("uid", "")
                    if meta_uid and audit_uid:
                        audit_uid_clean = audit_uid.replace("UID", "")
                        if meta_uid != audit_uid_clean:
                            self.errors.append(
                                f"UID mismatch: meta.uid={meta_uid}, "
                                f"audit.uid={audit_uid}"
                            )

        return self._result()

    def _validate_audit(self, audit: dict):
        """Validate audit object fields."""
        # Required keys
        missing_audit = REQUIRED_AUDIT_KEYS - set(audit.keys())
        if missing_audit:
            self.errors.append(f"Missing audit keys: {missing_audit}")

        # behavior_signature
        sig = audit.get("behavior_signature", {})
        if not isinstance(sig, dict):
            self.errors.append("behavior_signature is not a dict")
        else:
            missing_sig = REQUIRED_SIG_KEYS - set(sig.keys())
            if missing_sig:
                self.errors.append(f"Missing signature keys: {missing_sig}")
            else:
                self._validate_sig_values(sig)

        # pattern
        pattern = audit.get("behavior_pattern", "")
        if pattern and pattern not in VALID_PATTERNS:
            self.warnings.append(f"Unknown behavior pattern: {pattern}")

        # color
        color = audit.get("color", "")
        if color and color not in VALID_COLORS:
            self.warnings.append(f"Unknown audit color: {color}")

        # payload_hash
        ph = audit.get("payload_hash", "")
        if ph and (len(ph) != 16 or not all(c in "0123456789abcdef" for c in ph)):
            self.warnings.append(f"Suspicious payload_hash: {ph}")

    def _validate_sig_values(self, sig: dict):
        """Validate individual signature field values."""
        checks = [
            (sig["P"], VALID_P_VALUES, "P"),
            (sig["F"], VALID_F_VALUES, "F"),
            (isinstance(sig["T"], (int, float)), True, "T (number)"),
            (sig["E"], VALID_E_VALUES, "E"),
            (isinstance(sig["C"], (int, float)), True, "C (number)"),
            (isinstance(sig["R"], int) and sig["R"] >= 0, True, "R (int >= 0)"),
            (sig["A"], VALID_A_VALUES, "A"),
            (sig["X"], VALID_X_VALUES, "X"),
            (sig["Y"], VALID_Y_VALUES, "Y"),
            (isinstance(sig["Z"], (int, float)), True, "Z (number)"),
        ]
        for actual, expected, label in checks:
            if expected is True:
                if not actual:
                    self.warnings.append(f"Invalid {label}")
            else:
                if actual not in expected:
                    self.warnings.append(f"Invalid {label}: '{actual}'")

    def _result(self) -> dict:
        valid = len(self.errors) == 0
        if valid:
            summary = f"✅ VALID — {len(self.warnings)} warning(s)"
            if self.warnings:
                summary += f" ({', '.join(self.warnings[:2])})"
        else:
            summary = f"❌ INVALID — {len(self.errors)} error(s)"
        return {
            "valid": valid,
            "errors": self.errors,
            "warnings": self.warnings,
            "summary": summary,
        }


def quick_validate(wrapped: dict) -> bool:
    """Quick check: has required keys and valid DNA format?"""
    if not isinstance(wrapped, dict):
        return False
    if not set(wrapped.keys()).issuperset({"dna", "audit"}):
        return False
    dna = wrapped.get("dna", "")
    if not DNA_REGEX.match(dna):
        return False
    return True
