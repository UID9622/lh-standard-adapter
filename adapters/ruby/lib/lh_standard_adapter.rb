# frozen_string_literal: true
# LongHun Standard Adapter — Ruby
# DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷝Li-ADAPTER-CODE-WRAP-V1.0.0

require "digest"
require "json"

module LongHunAdapter
  P_VALUES = %w[HasPromise NoPromise].freeze
  F_VALUES = %w[Fulfilled Unfulfilled Partial].freeze
  E_VALUES = %w[Willing Perfunctory Resentful Numb].freeze
  A_VALUES = %w[Self Partner Family Outsider Public].freeze
  X_VALUES = %w[OverExplain Silent Genuine Indifferent].freeze
  Y_VALUES = %w[Changed Resisted Indifferent NoResponse].freeze

  LABEL_MAP = {
    "P" => { "HasPromise" => "7F-P-有承诺", "NoPromise" => "7F-P-无承诺" },
    "F" => { "Fulfilled" => "7F-F-已兑现", "Unfulfilled" => "7F-F-未兑现", "Partial" => "7F-F-部分兑现" },
    "E" => { "Willing" => "7F-E-心甘情愿", "Perfunctory" => "7F-E-敷衍", "Resentful" => "7F-E-怨恨", "Numb" => "7F-E-麻木" },
    "A" => { "Self" => "7F-A-自己", "Partner" => "7F-A-伴侣", "Family" => "7F-A-家庭", "Outsider" => "7F-A-外人", "Public" => "7F-A-公众" },
    "X" => { "OverExplain" => "7F-X-过度解释", "Silent" => "7F-X-沉默", "Genuine" => "7F-X-真诚", "Indifferent" => "7F-X-冷漠" },
    "Y" => { "Changed" => "7F-Y-改正", "Resisted" => "7F-Y-抗拒", "Indifferent" => "7F-Y-无视", "NoResponse" => "7F-Y-无响应" },
  }.freeze

  PATTERNS = {
    "MODE-DefensiveDefaulter"   => "Promises fail + over-explains to deflect",
    "MODE-ExternalTrustSpender" => "Keeps promises to outsiders at inner-circle expense",
    "MODE-InternalDestroyer"    => "Breaks promises with indifference, no correction",
    "MODE-Fluctuating"          => "High volatility in commitment-to-fulfillment ratio",
    "MODE-StableDisciplined"    => "Consistent, reliable execution",
  }.freeze

  class << self
    def wrap(data, task_type: "default", persona: "P04")
      body = "ADAPTER-#{task_type.upcase}-WRAP-V1.0"
      hash8 = Digest::SHA256.hexdigest("#{stem_branch}#{hexagram}#{body}")[0, 8]
      dna = "#LongHun⚡️#{stem_branch}·#{hexagram}-#{body}-#{hash8}"

      sig = default_signature
      pattern = classify(sig)
      labels = make_labels(sig, pattern)
      col = determine_color(pattern, sig["R"])

      payload_hash = Digest::SHA256.hexdigest(JSON.generate(data))[0, 16]
      {
        "dna" => dna,
        "audit" => {
          "audit_version" => "v1.0",
          "uid" => "UID#{uid}",
          "persona" => persona,
          "task_type" => task_type,
          "behavior_signature" => sig,
          "behavior_pattern" => pattern,
          "behavior_labels" => labels,
          "color" => col,
          "timestamp" => Time.now.utc.iso8601,
          "payload_hash" => payload_hash,
        },
        "payload" => data,
        "meta" => { "adapter_version" => "1.0.0", "uid" => uid, "format" => "longhun-v∞" },
      }
    end

    def validate(wrapped)
      errors = []
      warnings = []
      unless wrapped.is_a?(Hash) && !wrapped.empty?
        errors << "Input is not a non-empty dict"
        return { valid: false, errors: errors, warnings: warnings, summary: "❌ INVALID — #{errors.size} error(s)" }
      end
      unless wrapped.key?("dna") && wrapped.key?("audit") && wrapped.key?("payload") && wrapped.key?("meta")
        errors << "Missing top-level keys"
      end
      errors
      { valid: errors.empty?, errors: errors, warnings: warnings, summary: errors.empty? ? "✅ VALID" : "❌ INVALID" }
    end

    def quick_validate(wrapped)
      wrapped.is_a?(Hash) && wrapped.key?("dna") && wrapped.key?("audit")
    end

    private

    def default_signature
      { "P" => "HasPromise", "F" => "Fulfilled", "T" => 0.0, "E" => "Willing", "C" => 0, "R" => 0,
        "A" => "Self", "X" => "Genuine", "Y" => "NoResponse", "Z" => 1.0 }
    end

    def classify(sig)
      return "MODE-DefensiveDefaulter" if sig["F"] == "Unfulfilled" && sig["X"] == "OverExplain"
      return "MODE-ExternalTrustSpender" if sig["F"] == "Fulfilled" && sig["A"] == "Outsider"
      return "MODE-InternalDestroyer" if sig["F"] == "Unfulfilled" && sig["Y"] == "Indifferent"
      return "MODE-Fluctuating" if sig["Z"].to_f > 2.0
      "MODE-StableDisciplined"
    end

    def determine_color(pattern, repeat)
      return "🔴" if pattern == "MODE-InternalDestroyer"
      return "🟡" if pattern == "MODE-Fluctuating" && repeat.to_i > 3
      return "🟡" if pattern == "MODE-DefensiveDefaulter" && repeat.to_i > 2
      "🟢"
    end

    def make_labels(sig, pattern)
      labels = %w[P F E A X Y].map { |f| LABEL_MAP[f][sig[f]] }.compact
      labels << pattern
    end

    def stem_branch
      s = %w[JiaZi YiChou BingYin DingMao WuChen JiSi GengWu XinWei RenShen GuiYou JiaXu YiHai BingZi DingChou WuYin JiMao GengChen XinSi RenWu GuiWei JiaShen YiYou BingXu DingHai WuZi JiChou GengYin XinMao RenChen GuiSi]
      s[(Time.now.year + Time.now.month + Time.now.day) % s.length]
    end

    def hexagram
      "䷝Li"
    end
  end
end
