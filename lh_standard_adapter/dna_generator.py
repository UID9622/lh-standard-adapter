"""
DNA Generator — v∞ format traceability code generation.

DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-DNA-GENERATOR-v1.0.0
"""

import hashlib
from datetime import datetime, timezone, timedelta

# --- Heavenly Stems and Earthly Branches ---

TIAN_GAN = ["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"]
DI_ZHI = ["Zi", "Chou", "Yin", "Mao", "Chen", "Si",
           "Wu", "Wei", "Shen", "You", "Xu", "Hai"]

SHI_CHEN = ["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi",
            "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"]

# --- I Ching Hexagrams ---

HEXAGRAMS = [
    {"symbol": "䷀", "en_name": "Qian", "cn_name": "乾", "domain": "governance"},
    {"symbol": "䷁", "en_name": "Kun", "cn_name": "坤", "domain": "archive"},
    {"symbol": "䷂", "en_name": "Zhun", "cn_name": "屯", "domain": "init"},
    {"symbol": "䷃", "en_name": "Meng", "cn_name": "蒙", "domain": "learn"},
    {"symbol": "䷄", "en_name": "Xu", "cn_name": "需", "domain": "async"},
    {"symbol": "䷅", "en_name": "Song", "cn_name": "讼", "domain": "legal"},
    {"symbol": "䷜", "en_name": "Kan", "cn_name": "坎", "domain": "engine"},
    {"symbol": "䷝", "en_name": "Li", "cn_name": "离", "domain": "audit"},
    {"symbol": "䷲", "en_name": "Zhen", "cn_name": "震", "domain": "security"},
    {"symbol": "䷳", "en_name": "Gen", "cn_name": "艮", "domain": "privacy"},
    {"symbol": "䷸", "en_name": "Xun", "cn_name": "巽", "domain": "deploy"},
    {"symbol": "䷹", "en_name": "Dui", "cn_name": "兑", "domain": "trust"},
    {"symbol": "䷾", "en_name": "JiJi", "cn_name": "既济", "domain": "complete"},
    {"symbol": "䷿", "en_name": "WeiJi", "cn_name": "未济", "domain": "progress"},
]

# --- Task-to-hexagram domain mapping ---

TASK_HEXAGRAM_MAP = {
    "default": "governance",
    "code": "engine",
    "deploy": "deploy",
    "audit": "audit",
    "security": "security",
    "archive": "archive",
    "init": "init",
    "learn": "learn",
    "legal": "legal",
    "privacy": "privacy",
    "trust": "trust",
    "complete": "complete",
    "progress": "progress",
}


class DNAGenerator:
    """
    Generate v∞ format DNA traceability codes.

    Format: #LongHun⚡️{StemBranch}·{Hexagram}-{ModulePath}-{Hash8}
    """

    CYCLE_YEAR = 1984  # JiaZi year reference
    CYCLE_MONTH = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0]  # Month stems

    def __init__(self, uid: str = "9622", device: str = "HM-9622-001",
                 locale: str = "Asia/Shanghai"):
        self.uid = uid
        self.device = device
        self.locale = locale

    def generate(self, task_type: str = "default", action: str = "WRAP",
                 version: str = None) -> str:
        """Generate a full DNA traceability string."""
        tz = timezone(timedelta(hours=8))
        now = datetime.now(tz)

        stem = self._compute_stem_branch(now)
        hexagram = self._select_hexagram(task_type)
        version = version or "V1.0"

        body = f"ADAPTER-{task_type.upper()}-{action.upper()}-{version}"

        raw = (
            f"{stem['year']}{stem['month']}{stem['day']}{stem['shichen']}"
            f"{hexagram['symbol']}{hexagram['en_name']}"
            f"{body}"
            f"{self.device}"
            f"{now.isoformat()}"
        )
        hash8 = hashlib.sha256(raw.encode("utf-8")).hexdigest()[:8]

        return (
            f"#LongHun⚡️{stem['year']}·{stem['month']}·{stem['day']}·{stem['shichen']}"
            f"·{hexagram['symbol']}{hexagram['en_name']}"
            f"-{body}-{hash8}"
        )

    def _compute_stem_branch(self, dt: datetime) -> dict:
        """Compute Heavenly Stem + Earthly Branch for a datetime."""
        year_stem_idx = (dt.year - self.CYCLE_YEAR) % 10
        year_branch_idx = (dt.year - self.CYCLE_YEAR) % 12

        month_stem_idx = (self.CYCLE_MONTH[(dt.year - self.CYCLE_YEAR) % 10] +
                          (dt.month - 1)) % 10 if self.CYCLE_MONTH[
            (dt.year - self.CYCLE_YEAR) % 10
        ] is not None else (dt.month * 2) % 10
        month_branch_idx = (dt.month + 1) % 12

        day_stem_idx = (dt.year - 1900 + (dt.year - 1900) // 4 +
                        dt.timetuple().tm_yday) % 10
        day_branch_idx = (dt.year - 1900 + (dt.year - 1900) // 4 +
                          dt.timetuple().tm_yday) % 12

        shichen_idx = dt.hour // 2

        return {
            "year": TIAN_GAN[year_stem_idx] + DI_ZHI[year_branch_idx],
            "month": TIAN_GAN[month_stem_idx % 10] + DI_ZHI[month_branch_idx],
            "day": TIAN_GAN[day_stem_idx] + DI_ZHI[day_branch_idx],
            "shichen": SHI_CHEN[shichen_idx],
        }

    def _select_hexagram(self, task_type: str) -> dict:
        """Select I Ching hexagram based on task type."""
        domain = TASK_HEXAGRAM_MAP.get(task_type, "governance")
        candidates = [h for h in HEXAGRAMS if h["domain"] == domain]
        if candidates:
            return candidates[0]
        return HEXAGRAMS[0]  # Default: Qian (governance)


# Convenience function
_generator = DNAGenerator()


def generate_dna(task_type: str = "default", action: str = "WRAP",
                 version: str = None) -> str:
    """Quick one-shot DNA generation."""
    return _generator.generate(task_type=task_type, action=action, version=version)
