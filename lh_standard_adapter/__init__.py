"""
lh_standard_adapter — LongHun Standard Adapter v1.0.0

DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0
Author: LongHun Core · UID9622 · 龍芯北辰
License: CC BY-NC-SA 4.0

Open the standard. Guard the engine.

This adapter is an open-source shell tool. It wraps JSON payloads
with DNA traceability and seven-factor behavioral audit metadata.
Core compiler, training scripts, and algorithm logic are protected
Chinese independent intellectual property.
"""

__version__ = "1.0.0"
__author__ = "LongHun Core · UID9622 · 龍芯北辰"
__license__ = "CC BY-NC-SA 4.0"
__dna__ = "#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0-4f7a3b1c"

from .dna_generator import DNAGenerator, generate_dna
from .audit_wrapper import AuditWrapper, audit_wrap
from .validator import Validator, quick_validate


class LongHunAdapter:
    """
    LongHun Standard Adapter — wrap JSON payloads with DNA traceability
    and seven-factor behavioral audit metadata.

    Usage:
        adapter = LongHunAdapter(uid="9622", device="HM-9622-001")
        result = adapter.wrap(data={"code": "print('hello')"}, task_type="code")
        validation = adapter.validate(result)
    """

    def __init__(self, uid: str = "9622", device: str = "HM-9622-001", locale: str = "Asia/Shanghai"):
        self.uid = uid
        self.device = device
        self.locale = locale
        self._dna_gen = DNAGenerator(uid=uid, device=device, locale=locale)
        self._audit = AuditWrapper(uid=uid)
        self._validator = Validator()

    def wrap(self, data, task_type: str = "default", persona: str = "P04",
             action: str = "WRAP", version: str = None) -> dict:
        """
        Wrap a payload with DNA traceability and audit metadata.

        Args:
            data: Raw payload (dict, list, or JSON-serializable)
            task_type: Task category (code, deploy, audit, default)
            persona: Persona identifier (P04-Luban, P00-Wenxin, etc.)
            action: Action descriptor (WRAP, GENERATE, DEPLOY, AUDIT)
            version: Optional version override

        Returns:
            dict with keys: dna, audit, payload, meta
        """
        from datetime import datetime, timezone, timedelta

        # Generate DNA
        dna = self._dna_gen.generate(
            task_type=task_type,
            action=action,
            version=version,
        )

        # Generate audit wrapper
        audit = self._audit.wrap(
            payload=data,
            task_type=task_type,
            persona=persona,
        )

        return {
            "dna": dna,
            "audit": audit,
            "payload": data,
            "meta": {
                "adapter_version": __version__,
                "uid": self.uid,
                "device": self.device,
                "task_type": task_type,
                "persona": persona,
                "generated_at": datetime.now(
                    timezone(timedelta(hours=8))
                ).isoformat(),
                "format": "longhun-v∞",
            },
        }

    def validate(self, wrapped: dict) -> dict:
        """
        Validate a wrapped payload.

        Args:
            wrapped: Dict produced by .wrap()

        Returns:
            dict with keys: valid, errors, warnings, summary
        """
        return self._validator.validate(wrapped)

    def get_schemas(self) -> dict:
        """
        Get JSON Schemas for DNA and Audit formats.

        Returns:
            dict with keys: dna_schema, audit_schema
        """
        from .schemas import DNA_SCHEMA, AUDIT_SCHEMA
        return {
            "dna_schema": DNA_SCHEMA,
            "audit_schema": AUDIT_SCHEMA,
        }


def wrap(data, task_type: str = "default", persona: str = "P04",
         uid: str = "9622", device: str = "HM-9622-001") -> dict:
    """
    Convenience one-shot wrapper.

    Args:
        data: Raw payload
        task_type: Task category
        persona: Persona identifier
        uid: User identifier
        device: Device identifier

    Returns:
        Wrapped dict with dna, audit, payload, meta
    """
    adapter = LongHunAdapter(uid=uid, device=device)
    return adapter.wrap(data=data, task_type=task_type, persona=persona)


__all__ = [
    "LongHunAdapter",
    "DNAGenerator",
    "AuditWrapper",
    "Validator",
    "wrap",
    "generate_dna",
    "audit_wrap",
    "quick_validate",
    "__version__",
    "__author__",
    "__license__",
    "__dna__",
]
