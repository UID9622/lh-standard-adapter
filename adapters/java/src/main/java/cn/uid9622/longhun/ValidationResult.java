1|package cn.uid9622.longhun;
2|
3|import javax.annotation.Nullable;
4|import javax.annotation.concurrent.Immutable;
5|import java.util.ArrayList;
6|import java.util.Collections;
7|import java.util.List;
8|import java.util.Objects;
9|
10|/**
11| * Immutable result of a {@link LongHunAdapter#validate} operation.
12| *
13| * <p>Contains the validation status, detailed error messages, warnings,
14| * and a human-readable summary string.
15| *
16| * <p>This class is designed to be Kotlin-friendly with null-safety annotations
17| * and immutable collections.
18| *
19| * @since 1.0.0
20| */
21|@Immutable
22|public final class ValidationResult {
23|
24|    private final boolean valid;
25|    private final List<String> errors;
26|    private final List<String> warnings;
27|    private final String summary;
28|
29|    /**
30|     * Constructs a new {@code ValidationResult}.
31|     *
32|     * @param valid    whether the wrapped payload is valid
33|     * @param errors   list of validation error messages (non-null, copied defensively)
34|     * @param warnings list of validation warning messages (non-null, copied defensively)
35|     * @param summary  human-readable summary string
36|     * @throws NullPointerException if errors, warnings, or summary is null
37|     */
38|    public ValidationResult(boolean valid,
39|                            @Nullable List<String> errors,
40|                            @Nullable List<String> warnings,
41|                            String summary) {
42|        this.valid = valid;
43|        this.errors = errors != null
44|                ? Collections.unmodifiableList(new ArrayList<>(errors))
45|                : Collections.emptyList();
46|        this.warnings = warnings != null
47|                ? Collections.unmodifiableList(new ArrayList<>(warnings))
48|                : Collections.emptyList();
49|        this.summary = Objects.requireNonNull(summary, "summary must not be null");
50|    }
51|
52|    /**
53|     * Returns whether the wrapped payload is structurally valid.
54|     *
55|     * @return {@code true} if no errors were found
56|     */
57|    public boolean isValid() {
58|        return valid;
59|    }
60|
61|    /**
62|     * Returns an unmodifiable list of error messages.
63|     *
64|     * @return non-null, possibly empty list of errors
65|     */
66|    public List<String> getErrors() {
67|        return errors;
68|    }
69|
70|    /**
71|     * Returns an unmodifiable list of warning messages.
72|     *
73|     * @return non-null, possibly empty list of warnings
74|     */
75|    public List<String> getWarnings() {
76|        return warnings;
77|    }
78|
79|    /**
80|     * Returns a human-readable summary of the validation result.
81|     *
82|     * @return summary string, never null
83|     */
84|    public String getSummary() {
85|        return summary;
86|    }
87|
88|    @Override
89|    public boolean equals(Object o) {
90|        if (this == o) return true;
91|        if (o == null || getClass() != o.getClass()) return false;
92|        ValidationResult that = (ValidationResult) o;
93|        return valid == that.valid
94|                && errors.equals(that.errors)
95|                && warnings.equals(that.warnings)
96|                && summary.equals(that.summary);
97|    }
98|
99|    @Override
100|    public int hashCode() {
101|        int result = (valid ? 1 : 0);
102|        result = 31 * result + errors.hashCode();
103|        result = 31 * result + warnings.hashCode();
104|        result = 31 * result + summary.hashCode();
105|        return result;
106|    }
107|
108|    @Override
109|    public String toString() {
110|        return "ValidationResult{"
111|                + "valid=" + valid
112|                + ", errors=" + errors
113|                + ", warnings=" + warnings
114|                + ", summary='" + summary + '\''
115|                + '}';
116|    }
117|}