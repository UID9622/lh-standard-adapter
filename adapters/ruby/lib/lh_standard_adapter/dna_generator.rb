require 'digest'
require 'time'

module LongHun
  class DNAGenerator
    TIAN_GAN = ["Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"].freeze
    DI_ZHI = ["Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"].freeze
    SHI_CHEN = ["ZiShi", "ChouShi", "YinShi", "MaoShi", "ChenShi", "SiShi", "WuShi", "WeiShi", "ShenShi", "YouShi", "XuShi", "HaiShi"].freeze

    HEXAGRAMS = [
      { symbol: "䷀", en_name: "Qian", domain: "governance" },
      { symbol: "䷁", en_name: "Kun", domain: "archive" },
      { symbol: "䷂", en_name: "Zhun", domain: "init" },
      { symbol: "䷃", en_name: "Meng", domain: "learn" },
      { symbol: "䷄", en_name: "Xu", domain: "async" },
      { symbol: "䷅", en_name: "Song", domain: "legal" },
      { symbol: "䷜", en_name: "Kan", domain: "engine" },
      { symbol: "䷝", en_name: "Li", domain: "audit" },
      { symbol: "䷲", en_name: "Zhen", domain: "security" },
      { symbol: "䷳", en_name: "Gen", domain: "privacy" },
      { symbol: "䷸", en_name: "Xun", domain: "deploy" },
      { symbol: "䷹", en_name: "Dui", domain: "trust" },
      { symbol: "䷾", en_name: "JiJi", domain: "complete" },
      { symbol: "䷿", en_name: "WeiJi", domain: "progress" }
    ].freeze

    TASK_HEXAGRAM_MAP = {
      "default" => "governance",
      "code" => "engine",
      "deploy" => "deploy",
      "audit" => "audit",
      "security" => "security",
      "archive" => "archive",
      "init" => "init",
      "learn" => "learn",
      "legal" => "legal",
      "privacy" => "privacy",
      "trust" => "trust",
      "complete" => "complete",
      "progress" => "progress"
    }.freeze

    attr_reader :uid, :device

    def initialize(uid: "9622", device: "HM-9622-001")
      @uid = uid
      @device = device
      @cycle_year = 1984
      @cycle_month = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0]
    end

    def generate(task_type = "default", action = "WRAP", version = "V1.0")
      now = Time.now.getlocal("+08:00")
      stem = compute_stem_branch(now)
      hexagram = select_hexagram(task_type)
      body = "ADAPTER-#{task_type.upcase}-#{action.upcase}-#{version}"

      raw = "#{stem[:year]}#{stem[:month]}#{stem[:day]}#{stem[:shichen]}#{hexagram[:symbol]}#{hexagram[:en_name]}#{body}#{@device}#{now.iso8601}"
      hash8 = Digest::SHA256.hexdigest(raw)[0..7]

      "#LongHun⚡️#{stem[:year]}·#{stem[:month]}·#{stem[:day]}·#{stem[:shichen]}·#{hexagram[:symbol]}#{hexagram[:en_name]}-#{body}-#{hash8}"
    end

    private

    def compute_stem_branch(dt)
      year = dt.year
      year_stem_idx = ((year - @cycle_year) % 10).abs
      year_branch_idx = ((year - @cycle_year) % 12).abs

      month = dt.month - 1
      month_stem_idx = ((@cycle_month[year_stem_idx] + month) % 10).abs
      month_branch_idx = ((month + 2) % 12).abs

      day_of_year = dt.yday
      day_stem_idx = ((year - 1900 + (year - 1900) / 4 + day_of_year) % 10).abs
      day_branch_idx = ((year - 1900 + (year - 1900) / 4 + day_of_year) % 12).abs

      shichen_idx = dt.hour / 2

      {
        year: TIAN_GAN[year_stem_idx] + DI_ZHI[year_branch_idx],
        month: TIAN_GAN[month_stem_idx] + DI_ZHI[month_branch_idx],
        day: TIAN_GAN[day_stem_idx] + DI_ZHI[day_branch_idx],
        shichen: SHI_CHEN[shichen_idx % 12]
      }
    end

    def select_hexagram(task_type)
      domain = TASK_HEXAGRAM_MAP[task_type] || "governance"
      HEXAGRAMS.find { |h| h[:domain] == domain } || HEXAGRAMS[0]
    end
  end
end
