require 'digest'
require 'time'
require 'json'

module Lh
  class AuditWrapper
    LABEL_MAP = {
      'P' => { 'HasPromise' => '7F-P-有承诺', 'NoPromise' => '7F-P-无承诺' },
      'F' => { 'Fulfilled' => '7F-F-已兑现', 'Unfulfilled' => '7F-F-未兑现', 'Partial' => '7F-F-部分兑现' },
      'E' => { 'Willing' => '7F-E-心甘情愿', 'Perfunctory' => '7F-E-敷衍', 'Resentful' => '7F-E-怨恨', 'Numb' => '7F-E-麻木' },
      'A' => { 'Self' => '7F-A-自己', 'Partner' => '7F-A-伴侣', 'Family' => '7F-A-家庭', 'Outsider' => '7F-A-外人', 'Public' => '7F-A-公众' },
      'X' => { 'OverExplain' => '7F-X-过度解释', 'Silent' => '7F-X-沉默', 'Genuine' => '7F-X-真诚', 'Indifferent' => '7F-X-冷漠' },
      'Y' => { 'Changed' => '7F-Y-改正', 'Resisted' => '7F-Y-抗拒', 'Indifferent' => '7F-Y-无视', 'NoResponse' => '7F-Y-无响应' },
    }.freeze

    def initialize(uid = '9622'); @uid = uid; end

    def wrap(payload, task_type = 'default', persona = 'P04')
      sig = { 'P' => 'HasPromise', 'F' => 'Fulfilled', 'T' => 0.0, 'E' => 'Willing',
              'C' => 0, 'R' => 0, 'A' => 'Self', 'X' => 'Genuine', 'Y' => 'NoResponse', 'Z' => 1.0 }
      pattern = classify(sig)
      labels = make_labels(sig, pattern)
      color = determine_color(pattern, 0)
      payload_json = payload.to_json
      payload_hash = Digest::SHA256.hexdigest(payload_json)[0, 16]

      { 'audit_version' => 'v1.0', 'uid' => "UID#{@uid}", 'persona' => persona,
        'task_type' => task_type, 'behavior_signature' => sig, 'behavior_pattern' => pattern,
        'behavior_labels' => labels, 'color' => color,
        'timestamp' => Time.now.getlocal('+08:00').iso8601,
        'payload_hash' => payload_hash }
    end

    def classify(sig)
      f = sig['F']; x = sig['X']; a = sig['A']; y = sig['Y']; z = sig['Z'].to_f
      return 'MODE-DefensiveDefaulter' if f == 'Unfulfilled' && x == 'OverExplain'
      return 'MODE-ExternalTrustSpender' if f == 'Fulfilled' && a == 'Outsider'
      return 'MODE-InternalDestroyer' if f == 'Unfulfilled' && y == 'Indifferent'
      return 'MODE-Fluctuating' if z > 2.0
      'MODE-StableDisciplined'
    end

    def determine_color(pattern, repeat)
      return '🔴' if pattern == 'MODE-InternalDestroyer'
      return '🟡' if pattern == 'MODE-Fluctuating' && repeat > 3
      return '🟡' if pattern == 'MODE-DefensiveDefaulter' && repeat > 2
      '🟢'
    end

    def make_labels(sig, pattern)
      labels = []
      %w[P F E A X Y].each do |k|
        v = sig[k]
        labels << LABEL_MAP[k][v] if LABEL_MAP[k] && LABEL_MAP[k][v]
      end
      labels << pattern
      labels
    end
  end
end
