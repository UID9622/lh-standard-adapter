package cn.uid9622.longhun;

/**
 * JSON Schema constants for LongHun DNA Traceability Protocol v1.0.
 *
 * <p>This class provides the canonical schema definitions as string constants.
 * These match the reference implementation byte-for-byte.
 */
public final class Schemas {

    private Schemas() {
        // Utility class — no instantiation
    }

    /**
     * DNA format specification string (human-readable).
     * Format: {@code #LongHun⚡️{YearStem}·{MonthStem}·{DayStem}·{ShiChen}·{HexagramSymbol}{HexagramName}-{Body}-{hash8}}
     */
    public static final String DNA_SCHEMA =
            "#LongHun⚡️{YearStem}·{MonthStem}·{DayStem}·{ShiChen}·{HexagramSymbol}{HexagramName}-{Body}-{hash8}";

    /**
     * Validation regex for DNA codes. Seven capture groups match stem-branch fields,
     * hexagram name, body, and hex hash.
     */
    public static final String DNA_REGEX =
            "^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$";

    /**
     * Top-level keys required in a wrapped payload object.
     */
    public static final String[] REQUIRED_TOP_KEYS = {"dna", "audit", "payload", "meta"};

    /**
     * Keys required inside the audit block.
     */
    public static final String[] REQUIRED_AUDIT_KEYS = {
            "audit_version", "uid", "behavior_signature",
            "behavior_pattern", "behavior_labels", "color"
    };

    /**
     * Keys required inside the behavior_signature block (7 + 3 numeric = 10 total).
     */
    public static final String[] REQUIRED_SIG_KEYS = {
            "P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"
    };

    /**
     * Valid tri-color audit values.
     */
    public static final String[] VALID_COLORS = {"🟢", "🟡", "🔴"};

    /**
     * Valid behavior pattern names.
     */
    public static final String[] VALID_PATTERNS = {
            "MODE-DefensiveDefaulter",
            "MODE-ExternalTrustSpender",
            "MODE-InternalDestroyer",
            "MODE-Fluctuating",
            "MODE-StableDisciplined"
    };

    // Seven-factor value sets
    public static final String[] VALID_P_VALUES = {"HasPromise", "NoPromise"};
    public static final String[] VALID_F_VALUES = {"Fulfilled", "Unfulfilled", "Partial"};
    public static final String[] VALID_E_VALUES = {"Willing", "Perfunctory", "Resentful", "Numb"};
    public static final String[] VALID_A_VALUES = {"Self", "Partner", "Family", "Outsider", "Public"};
    public static final String[] VALID_X_VALUES = {"OverExplain", "Silent", "Genuine", "Indifferent"};
    public static final String[] VALID_Y_VALUES = {"Changed", "Resisted", "Indifferent", "NoResponse"};

    /**
     * Audit record JSON template (for documentation / reference).
     */
    public static final String AUDIT_SCHEMA =
            "{\n" +
            "  \"dna\": \"#LongHun⚡️...\",\n" +
            "  \"audit\": {\n" +
            "    \"audit_version\": \"v1.0\",\n" +
            "    \"uid\": \"UID9622\",\n" +
            "    \"behavior_signature\": {\n" +
            "      \"P\": \"HasPromise\",\n" +
            "      \"F\": \"Fulfilled\",\n" +
            "      \"T\": 0.0,\n" +
            "      \"E\": \"Willing\",\n" +
            "      \"C\": 0,\n" +
            "      \"R\": 0,\n" +
            "      \"A\": \"Self\",\n" +
            "      \"X\": \"Genuine\",\n" +
            "      \"Y\": \"NoResponse\",\n" +
            "      \"Z\": 1.0\n" +
            "    },\n" +
            "    \"behavior_pattern\": \"MODE-StableDisciplined\",\n" +
            "    \"behavior_labels\": [\"7F-P-有承诺\", \"7F-F-已兑现\", \"MODE-StableDisciplined\"],\n" +
            "    \"color\": \"🟢\",\n" +
            "    \"timestamp\": \"2026-07-24T13:00:00+08:00\",\n" +
            "    \"payload_hash\": \"a1b2c3d4e5f67890\"\n" +
            "  },\n" +
            "  \"payload\": {},\n" +
            "  \"meta\": {\n" +
            "    \"adapter_version\": \"1.0.0\",\n" +
            "    \"uid\": \"9622\",\n" +
            "    \"format\": \"longhun-v∞\"\n" +
            "  }\n" +
            "}";
}
