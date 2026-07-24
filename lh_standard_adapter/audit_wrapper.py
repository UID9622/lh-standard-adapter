"""
Audit Wrapper — seven-factor behavioral audit metadata generation.

DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-AUDIT-WRAPPER-v1.0.0
"""

import hashlib
import json
from datetime import datetime, timezone, timedelta

# --- Seven-Factor Value Sets (public standard) ---

P_VALUES = ["HasPromise", "NoPromise"]
F_VALUES = ["Fulfilled", "Unfulfilled", "Partial"]
E_VALUES = ["Willing", "Perfunctory", "Resentful", "Numb"]
A_VALUES = ["Self", "Partner", "Family", "Outsider", "Public"]
X_VALUES = ["OverExplain", "Silent", "Genuine", "Indifferent"]
Y_VALUES = ["Changed", "Resisted", "Indifferent", "NoResponse"]

# --- Behavior Pattern Classification ---

PATTERNS = {
    "MODE-DefensiveDefaulter": "Promises fail + over-explains to deflect",
    "MODE-ExternalTrustSpender": "Keeps promises to outsiders at inner-circle expense",
    "MODE-InternalDestroyer": "Breaks promises with indifference, no correction",
    "MODE-Fluctuating": "High volatility in commitment-to-fulfillment ratio",
    "MODE-StableDisciplined": "Consistent, reliable execution",
}

# --- Factor → Label Mapping (bilingual) ---

LABEL_MAP = {
    "P": {"HasPromise": "7F-P-有承诺", "NoPromise": "7F-P-无承诺"},
    "F": {"Fulfilled": "7F-F-已兑现", "Unfulfilled": "7F-F-未兑现", "Partial": "7F-F-部分兑现"},
    "E": {"Willing": "7F-E-心甘情愿", "Perfunctory": "7F-E-敷衍",
         "Resentful": "7F-E-怨恨", "Numb": "7F-E-麻木"},
    "A": {"Self": "7F-A-自己", "Partner": "7F-A-伴侣",
         "Family": "7F-A-家庭", "Outsider": "7F-A-外人", "Public": "7F-A-公众"},
    "X": {"OverExplain": "7F-X-过度解释", "Silent": "7F-X-沉默",
         "Genuine": "7F-X-真诚", "Indifferent": "7F-X-冷漠"},
    "Y": {"Changed": "7F-Y-改正", "Resisted": "7F-Y-抗拒",
         "Indifferent": "7F-Y-无视", "NoResponse": "7F-Y-无响应"},
}


class AuditWrapper:
    """
    Wrap payloads with seven-factor behavioral audit metadata.

    Core scoring algorithms (weights, neural network logic) are
    protected engine components and NOT included in this open shell.
    """

    def __init__(self, uid: str = "9622"):
        self.uid = uid

    def wrap(self, payload, task_type: str = "default",
             persona: str = "P04") -> dict:
        """
        Generate audit wrapper with seven-factor signature.

        Args:
            payload: Raw data to wrap
            task_type: Task category
            persona: Persona identifier

        Returns:
            dict with audit metadata
        """
        tz = timezone(timedelta(hours=8))
        now = datetime.now(tz)

        # Default signature (StableDisciplined baseline)
        signature = {
            "P": "HasPromise",
            "F": "Fulfilled",
            "T": 0.0,
            "E": "Willing",
            "C": 0,
            "R": 0,
            "A": "Self",
            "X": "Genuine",
            "Y": "NoResponse",
            "Z": 1.0,
        }

        pattern = self._classify(signature)
        labels = self._make_labels(signature, pattern)
        color = self._determine_color(pattern, signature["R"])

        # Payload hash (not for crypto, for integrity check)
        payload_json = json.dumps(payload, sort_keys=True, default=str,
                                   ensure_ascii=False)
        payload_hash = hashlib.sha256(payload_json.encode("utf-8")).hexdigest()[:16]

        return {
            "audit_version": "v1.0",
            "uid": f"UID{self.uid}",
            "persona": persona,
            "task_type": task_type,
            "behavior_signature": signature,
            "behavior_pattern": pattern,
            "behavior_labels": labels,
            "color": color,
            "timestamp": now.isoformat(),
            "payload_hash": payload_hash,
        }

    def _classify(self, sig: dict) -> str:
        """Classify seven-factor signature into behavior pattern."""
        f_val = sig.get("F", "")
        x_val = sig.get("X", "")
        a_val = sig.get("A", "")
        y_val = sig.get("Y", "")
        z_val = sig.get("Z", 1.0)

        if f_val == "Unfulfilled" and x_val == "OverExplain":
            return "MODE-DefensiveDefaulter"
        if f_val == "Fulfilled" and a_val == "Outsider":
            return "MODE-ExternalTrustSpender"
        if f_val == "Unfulfilled" and y_val == "Indifferent":
            return "MODE-InternalDestroyer"
        if z_val > 2.0:
            return "MODE-Fluctuating"
        return "MODE-StableDisciplined"

    def _make_labels(self, sig: dict, pattern: str) -> list:
        """Generate bilingual behavior labels from signature."""
        labels = []
        for factor in ["P", "F", "E", "A", "X", "Y"]:
            val = sig.get(factor, "")
            if factor in LABEL_MAP and val in LABEL_MAP[factor]:
                labels.append(LABEL_MAP[factor][val])
        labels.append(pattern)
        return labels

    def _determine_color(self, pattern: str, repeat: int) -> str:
        """Determine three-color audit tag."""
        if pattern == "MODE-InternalDestroyer":
            return "🔴"
        if pattern == "MODE-Fluctuating" and repeat > 3:
            return "🟡"
        if pattern == "MODE-DefensiveDefaulter" and repeat > 2:
            return "🟡"
        return "🟢"


# Convenience function
_wrapper = AuditWrapper()


def audit_wrap(payload, task_type: str = "default", persona: str = "P04") -> dict:
    """Quick one-shot audit wrapper."""
    return _wrapper.wrap(payload=payload, task_type=task_type, persona=persona)
