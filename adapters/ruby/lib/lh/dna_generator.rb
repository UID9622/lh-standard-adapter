require 'digest'
require 'time'

module Lh
  # Heavenly Stems and Earthly Branches
  TIAN_GAN = %w[Jia Yi Bing Ding Wu Ji Geng Xin Ren Gui].freeze
  DI_ZHI  = %w[Zi Chou Yin Mao Chen Si Wu Wei Shen You Xu Hai].freeze
  SHI_CHEN = %w[ZiShi ChouShi YinShi MaoShi ChenShi SiShi WuShi WeiShi ShenShi YouShi XuShi HaiShi].freeze

  CYCLE_YEAR = 1984
  CYCLE_MONTH = [2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0].freeze

  HEXAGRAMS = [
    { symbol: '䷀', en_name: 'Qian',  cn_name: '乾', domain: 'governance' },
    { symbol: '䷁', en_name: 'Kun',   cn_name: '坤', domain: 'archive' },
    { symbol: '䷂', en_name: 'Zhun',  cn_name: '屯', domain: 'init' },
    { symbol: '䷃', en_name: 'Meng',  cn_name: '蒙', domain: 'learn' },
    { symbol: '䷄', en_name: 'Xu',    cn_name: '需', domain: 'async' },
    { symbol: '䷅', en_name: 'Song',  cn_name: '讼', domain: 'legal' },
    { symbol: '䷜', en_name: 'Kan',   cn_name: '坎', domain: 'engine' },
    { symbol: '䷝', en_name: 'Li',    cn_name: '离', domain: 'audit' },
    { symbol: '䷲', en_name: 'Zhen',  cn_name: '震', domain: 'security' },
    { symbol: '䷳', en_name: 'Gen',   cn_name: '艮', domain: 'privacy' },
    { symbol: '䷸', en_name: 'Xun',   cn_name: '巽', domain: 'deploy' },
    { symbol: '䷹', en_name: 'Dui',   cn_name: '兑', domain: 'trust' },
    { symbol: '䷾', en_name: 'JiJi',  cn_name: '既济', domain: 'complete' },
    { symbol: '䷿', en_name: 'WeiJi', cn_name: '未济', domain: 'progress' },
  ].freeze

  TASK_HEXAGRAM_MAP = {
    'default' => 'governance', 'code' => 'engine', 'deploy' => 'deploy',
    'audit' => 'audit', 'security' => 'security', 'archive' => 'archive',
    'init' => 'init', 'learn' => 'learn', 'legal' => 'legal',
    'privacy' => 'privacy', 'trust' => 'trust',
    'complete' => 'complete', 'progress' => 'progress',
  }.freeze

  # DNA Generator
  class DnaGenerator
    def initialize(uid: '9622', device: 'HM-9622-001', locale: 'Asia/Shanghai')
      @uid = uid; @device = device; @locale = locale
    end

    def generate(task_type = 'default', action = 'WRAP', version = nil)
      tz = Time.now.getlocal('+08:00')
      stem = compute_stem_branch(tz)
      hexagram = select_hexagram(task_type)
      ver = version || 'V1.0'

      body = "ADAPTER-#{task_type.upcase}-#{action.upcase}-#{ver}"
      raw = "#{stem[:year]}#{stem[:month]}#{stem[:day]}#{stem[:shichen]}#{hexagram[:symbol]}#{hexagram[:en_name]}#{body}#{@device}#{tz.iso8601}"
      hash8 = Digest::SHA256.hexdigest(raw)[0, 8]

      "#LongHun\u26a1\ufe0f#{stem[:year]}\u00b7#{stem[:month]}\u00b7#{stem[:day]}\u00b7#{stem[:shichen]}\u00b7#{hexagram[:symbol]}#{hexagram[:en_name]}-#{body}-#{hash8}"
    end

    def compute_stem_branch(dt)
      y = dt.year; m = dt.month; yday = dt.yday
      ysi = ((y - CYCLE_YEAR) % 10 + 10) % 10
      ybi = ((y - CYCLE_YEAR) % 12 + 12) % 12
      cyc = ((y - CYCLE_YEAR) % 10 + 10) % 10
      base = CYCLE_MONTH[cyc]
      msi = base >= 0 ? (base + (m - 1)) % 10 : (m * 2) % 10
      mbi = (m + 1) % 12
      yo = y - 1900
      dsi = ((yo + yo / 4 + yday) % 10 + 10) % 10
      dbi = ((yo + yo / 4 + yday) % 12 + 12) % 12
      shi = [dt.hour / 2, 11].min
      { year: TIAN_GAN[ysi] + DI_ZHI[ybi],
        month: TIAN_GAN[msi] + DI_ZHI[mbi],
        day: TIAN_GAN[dsi] + DI_ZHI[dbi],
        shichen: SHI_CHEN[shi] }
    end

    def select_hexagram(task_type)
      domain = TASK_HEXAGRAM_MAP[task_type] || 'governance'
      HEXAGRAMS.find { |h| h[:domain] == domain } || HEXAGRAMS[0]
    end
  end
end
