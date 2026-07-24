package lh_adapter

// DNA_SCHEMA is the JSON schema for DNA traceability format validation.
var DNA_SCHEMA = map[string]interface{}{
	"$schema": "https://json-schema.org/draft/2020-12/schema",
	"title":   "LongHun DNA Traceability Format v∞",
	"type":    "string",
	"pattern": DNA_REGEX,
	"examples": []string{
		"#LongHun⚡️BingWu·GuiWei·JiaZi·WeiShi·䷾JiJi-ADAPTER-CODE-WRAP-V1.0-a3f8c1d9",
	},
}

// AUDIT_SCHEMA is the JSON schema for seven-factor behavioral audit records.
var AUDIT_SCHEMA = map[string]interface{}{
	"$schema": "https://json-schema.org/draft/2020-12/schema",
	"title":   "LongHun Seven-Factor Behavioral Audit v1.0",
	"type":    "object",
	"required": []string{"dna", "audit", "payload", "meta"},
	"properties": map[string]interface{}{
		"dna": map[string]interface{}{
			"type":        "string",
			"description": "DNA traceability identifier",
			"pattern":     DNA_REGEX,
		},
		"audit": map[string]interface{}{
			"type":     "object",
			"required": []string{"audit_version", "uid", "behavior_signature", "behavior_pattern", "behavior_labels", "color"},
			"properties": map[string]interface{}{
				"audit_version": map[string]interface{}{
					"type":    "string",
					"pattern": "^v\\d+\\.\\d+$",
				},
				"uid": map[string]interface{}{
					"type":    "string",
					"pattern": "^UID\\d+$",
				},
				"behavior_signature": map[string]interface{}{
					"type": "object",
					"required": []string{"P", "F", "T", "E", "C", "R", "A", "X", "Y", "Z"},
					"properties": map[string]interface{}{
						"P": map[string]interface{}{
							"type": "string",
							"enum": []string{"HasPromise", "NoPromise"},
						},
						"F": map[string]interface{}{
							"type": "string",
							"enum": []string{"Fulfilled", "Unfulfilled", "Partial"},
						},
						"T": map[string]interface{}{
							"type": "number",
						},
						"E": map[string]interface{}{
							"type": "string",
							"enum": []string{"Willing", "Perfunctory", "Resentful", "Numb"},
						},
						"C": map[string]interface{}{
							"type": "integer",
						},
						"R": map[string]interface{}{
							"type":    "integer",
							"minimum": 0,
						},
						"A": map[string]interface{}{
							"type": "string",
							"enum": []string{"Self", "Partner", "Family", "Outsider", "Public"},
						},
						"X": map[string]interface{}{
							"type": "string",
							"enum": []string{"OverExplain", "Silent", "Genuine", "Indifferent"},
						},
						"Y": map[string]interface{}{
							"type": "string",
							"enum": []string{"Changed", "Resisted", "Indifferent", "NoResponse"},
						},
						"Z": map[string]interface{}{
							"type": "number",
						},
					},
				},
				"behavior_pattern": map[string]interface{}{
					"type": "string",
					"enum": []string{
						"MODE-DefensiveDefaulter",
						"MODE-ExternalTrustSpender",
						"MODE-InternalDestroyer",
						"MODE-Fluctuating",
						"MODE-StableDisciplined",
					},
				},
				"behavior_labels": map[string]interface{}{
					"type":  "array",
					"items": map[string]interface{}{"type": "string"},
				},
				"color": map[string]interface{}{
					"type": "string",
					"enum": []string{"🟢", "🟡", "🔴"},
				},
			},
		},
		"payload": map[string]interface{}{
			"description": "Arbitrary payload data",
		},
		"meta": map[string]interface{}{
			"type":     "object",
			"required": []string{"adapter_version", "uid", "format"},
			"properties": map[string]interface{}{
				"adapter_version": map[string]interface{}{
					"type": "string",
				},
				"uid": map[string]interface{}{
					"type": "string",
				},
				"device": map[string]interface{}{
					"type": "string",
				},
				"task_type": map[string]interface{}{
					"type": "string",
				},
				"persona": map[string]interface{}{
					"type": "string",
				},
				"generated_at": map[string]interface{}{
					"type":   "string",
					"format": "date-time",
				},
				"format": map[string]interface{}{
					"type": "string",
				},
			},
		},
	},
}
