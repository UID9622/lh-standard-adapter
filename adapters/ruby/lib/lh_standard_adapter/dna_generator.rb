require "digest"
require "time"

module LhStandardAdapter
  TIAN_GAN = %w[Jia Yi Bing Ding Wu Ji Geng Xin Ren Gui].freeze
  DI_ZHI = %w[Zi Chou Yin Mao Chen Si Wu Wei Shen You Xu Hai].freeze
  SHI_CHEN = %w[ZiShi ChouShi YinShi MaoShi ChenShi SiShi WuShi WeiShi ShenShi YouShi XuShi HaiShi].freeze

  HEXAGRAMS = [
    {symbol:"䷀",en:"Qian",cn:"乾",domain:"governance"},
    {symbol:"䷁",en:"Kun",cn:"坤",domain:"archive"},
    {symbol:"䷂",en:"Zhun",cn:"屯",domain:"init"},
    {symbol:"䷃",en:"Meng",cn:"蒙",domain:"learn"},
    {symbol:"䷄",en:"Xu",cn:"需",domain:"async"},
    {symbol:"䷅",en:"Song",cn:"讼",domain:"legal"},
    {symbol:"䷜",en:"Kan",cn:"坎",domain:"engine"},
    {symbol:"䷝",en:"Li",cn:"离",domain:"audit"},
    {symbol:"䷲",en:"Zhen",cn:"震",domain:"security"},
    {symbol:"䷳",en:"Gen",cn:"艮",domain:"privacy"},
    {symbol:"䷸",en:"Xun",cn:"巽",domain:"deploy"},
    {symbol:"䷹",en:"Dui",cn:"兑",domain:"trust"},
    {symbol:"䷾",en:"JiJi",cn:"既济",domain:"complete"},
    {symbol:"䷿",en:"WeiJi",cn:"未济",domain:"progress"}
  ].freeze

  TASK_HEXAGRAM_MAP = {
    "default"=>"governance","code"=>"engine","deploy"=>"deploy","audit"=>"audit",
    "security"=>"security","archive"=>"archive","init"=>"init","learn"=>"learn",
    "legal"=>"legal","privacy"=>"privacy","trust"=>"trust","complete"=>"complete","progress"=>"progress"
  }.freeze

  class DNAGenerator
    CYCLE_YEAR = 1984
    CYCLE_MONTH = [2,4,6,8,10,0,2,4,6,8,10,0].freeze

    def initialize(uid: "9622", device: "HM-9622-001", locale: "Asia/Shanghai")
      @uid, @device, @locale = uid, device, locale
    end

    def generate(task_type: "default", action: "WRAP", version: nil)
      task_type = "default" if task_type.nil? || task_type.empty?
      action = "WRAP" if action.nil? || action.empty?
      version = "V1.0" if version.nil? || version.empty?

      now = Time.now.utc
      stem = compute_stem_branch(now)
      hex = select_hexagram(task_type)
      body = "ADAPTER-#{task_type.upcase}-#{action.upcase}-#{version}"
      raw = "#{stem[:year]}#{stem[:month]}#{stem[:day]}#{stem[:shichen]}#{hex[:symbol]}#{hex[:en]}#{body}#{@device}#{now.iso8601}"
      hash8 = Digest::SHA256.hexdigest(raw)[0,8]
      "#LongHun⚡️#{stem[:year]}·#{stem[:month]}·#{stem[:day]}·#{stem[:shichen]}·#{hex[:symbol]}#{hex[:en]}-#{body}-#{hash8}"
    end

    private

    def compute_stem_branch(dt)
      y, m, doy, h = dt.year, dt.month, dt.yday, dt.hour
      ys = (y - CYCLE_YEAR) % 10; yb = (y - CYCLE_YEAR) % 12
      mb = CYCLE_MONTH[(y - CYCLE_YEAR) % 10]
      ms = (mb + m - 1) % 10; mbr = (m + 1) % 12
      ds = (y - 1900 + (y - 1900) / 4 + doy) % 10
      db = (y - 1900 + (y - 1900) / 4 + doy) % 12
      si = h / 2

      { year: TIAN_GAN[ys] + DI_ZHI[yb],
        month: TIAN_GAN[ms] + DI_ZHI[mbr],
        day: TIAN_GAN[ds] + DI_ZHI[db],
        shichen: SHI_CHEN[si] }
    end

    def select_hexagram(task_type)
      domain = TASK_HEXAGRAM_MAP[task_type] || "governance"
      HEXAGRAMS.find { |h| h[:domain] == domain } || HEXAGRAMS.first
    end
  end
end
