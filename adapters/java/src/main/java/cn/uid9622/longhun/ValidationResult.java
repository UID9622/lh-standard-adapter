package cn.uid9622.longhun;

import java.util.List;

public class ValidationResult {
    private final boolean valid;
    private final List<String> errors, warnings;
    private final String summary;

    public ValidationResult(boolean valid, List<String> errors, List<String> warnings, String summary) {
        this.valid = valid; this.errors = errors; this.warnings = warnings; this.summary = summary;
    }
    public boolean isValid() { return valid; }
    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
    public String getSummary() { return summary; }
}
