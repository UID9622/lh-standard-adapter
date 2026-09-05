# frozen_string_literal: true

# LongHun Standard Adapter — Ruby
#
# DNA v∞ traceability generation + validation, mirroring the Python reference
# implementation so that all language adapters produce identical DNA prefixes
# (four Ganzhi pillars + hexagram + body) for the same task at the same instant
# under Asia/Shanghai time.

require "digest"
require "time"

module LongHun
  TIAN_GAN = %w[Jia Yi Bing Ding Wu Ji Geng Xin Ren Gui].freeze
  DI_ZHI = %w[Zi Chou Yin Mao Chen Si Wu Wei Shen You Xu Hai].freeze
  SHI_CHEN = %w[ZiShi ChouShi YinShi MaoShi ChenShi SiShi WuShi WeiShi ShenShi YouShi XuShi HaiShi].freeze
  CYCLE_YEAR = 1984 # JiaZi reference year
  CYCLE_MONTH = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0].freeze

  HEXAGRAMS = {
    governance: ["䷀", "Qian"],
    archive: ["䷁", "Kun"],
    init: ["䷂", "Zhun"],
    learn: ["䷃", "Meng"],
    async: ["䷄", "Xu"],
    legal: ["䷅", "Song"],
    engine: ["䷜", "Kan"],
    audit: ["䷝", "Li"],
    security: ["䷲", "Zhen"],
    privacy: ["䷳", "Gen"],
    deploy: ["䷸", "Xun"],
    trust: ["䷹", "Dui"],
    complete: ["䷾", "JiJi"],
    progress: ["䷿", "WeiJi"],
  }.freeze

  TASK_DOMAIN = {
    "default" => :governance,
    "code" => :engine,
    "deploy" => :deploy,
    "audit" => :audit,
    "security" => :security,
    "archive" => :archive,
    "init" => :init,
    "learn" => :learn,
    "legal" => :legal,
    "privacy" => :privacy,
    "trust" => :trust,
    "complete" => :complete,
    "progress" => :progress,
  }.freeze

  DNA_REGEX = %r{\A#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})\z}

  class Adapter
    attr_reader :uid, :device

    def initialize(uid = "9622", device = "HM-9622-001")
      @uid = uid
      @device = device
    end

    def generate_dna(task_type = "default", action = "WRAP", version = "V1.0")
      task_type = "default" if task_type.nil? || task_type.empty?
      action = "WRAP" if action.nil? || action.empty?
      version = "V1.0" if version.nil? || version.empty?

      # Asia/Shanghai wall-clock time (UTC+8).
      now = Time.now.getlocal("+08:00")
      base = now.year - LongHun::CYCLE_YEAR

      year_stem = LongHun::TIAN_GAN[base % 10] + LongHun::DI_ZHI[base % 12]

      month_idx = now.month
      m_stem = (LongHun::CYCLE_MONTH[base % 10] + month_idx - 1) % 10
      month_pillar = LongHun::TIAN_GAN[m_stem] + LongHun::DI_ZHI[(month_idx + 1) % 12]

      julian = now.year - 1900 + (now.year - 1900) / 4 + now.yday
      day_pillar = LongHun::TIAN_GAN[julian % 10] + LongHun::DI_ZHI[julian % 12]

      shichen = LongHun::SHI_CHEN[((now.hour + 1) / 2) % 12]

      symbol, en_name = hexagram_for(task_type)
      body = format("ADAPTER-%s-%s-%s", task_type.upcase, action.upcase, version.upcase)
      raw = "#{year_stem}#{month_pillar}#{day_pillar}#{shichen}#{symbol}#{en_name}#{body}#{@device}#{now.iso8601}"

      hash8 = Digest::SHA256.hexdigest(raw)[0, 8]
      format("#LongHun⚡️%s·%s·%s·%s·%s%s-%s-%s",
             year_stem, month_pillar, day_pillar, shichen, symbol, en_name, body, hash8)
    end

    def validate(dna)
      return false if dna.nil?
      dna.match?(LongHun::DNA_REGEX)
    end

    private

    def hexagram_for(task_type)
      domain = LongHun::TASK_DOMAIN.fetch(task_type, :governance)
      LongHun::HEXAGRAMS.fetch(domain)
    end
  end
end

# CLI demo used by the CI consistency check.
if __FILE__ == $PROGRAM_NAME
  puts LongHun::Adapter.new.generate_dna("code", "WRAP", "V1.0")
end
