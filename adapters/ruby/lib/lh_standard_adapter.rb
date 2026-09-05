require 'digest'
require 'time'

module LongHun
  class Adapter
    DNA_REGEX = /^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([䷀-䷿][A-Za-z]+)-(.+)-([a-f0-9]{8})$/

    def initialize(uid = "9622", device = "HM-9622-001")
      @uid = uid
      @device = device
    end

    def generate_dna(task_type = "default", action = "WRAP", version = "V1.0")
      body = "ADAPTER-#{task_type.upcase}-#{action.upcase}-#{version}"
      raw = "BingWuGuiWeiJiaZiZiShi䷾JiJi#{body}#{@device}#{Time.now.utc.iso8601}"
      hash8 = Digest::SHA256.hexdigest(raw)[0..7]
      "#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-#{body}-#{hash8}"
    end

    def validate(dna)
      !dna.nil? && DNA_REGEX.match?(dna)
    end
  end
end
