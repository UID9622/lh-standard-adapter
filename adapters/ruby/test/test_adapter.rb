require "minitest/autorun"
require_relative "../lib/lh_standard_adapter"

class TestDNAGenerator < Minitest::Test
  def test_instance
    g = LhStandardAdapter::DNAGenerator.new
    assert_equal "9622", g.instance_variable_get(:@uid)
  end

  def test_generate_prefix
    g = LhStandardAdapter::DNAGenerator.new
    dna = g.generate
    assert dna.start_with?("#LongHun⚡️")
  end

  def test_generate_hash8
    g = LhStandardAdapter::DNAGenerator.new
    dna = g.generate
    hash8 = dna.split("-").last
    assert_match(/^[a-f0-9]{8}$/, hash8)
  end

  def test_generate_unique
    g = LhStandardAdapter::DNAGenerator.new
    dnas = 10.times.map { |i| g.generate(task_type:"code", action:"GEN", version:"V#{i}") }
    assert_equal 10, dnas.uniq.length
  end
end

class TestAuditWrapper < Minitest::Test
  def test_wrap_version
    w = LhStandardAdapter::AuditWrapper.new
    r = w.wrap({"x"=>1})
    assert_equal "v1.0", r["audit_version"]
    assert_equal "UID9622", r["uid"]
  end

  def test_wrap_signature
    w = LhStandardAdapter::AuditWrapper.new
    sig = w.wrap({})["behavior_signature"]
    %w[P F T E C R A X Y Z].each { |k| assert sig.key?(k), "missing #{k}" }
  end

  def test_default_pattern
    w = LhStandardAdapter::AuditWrapper.new
    assert_equal "MODE-StableDisciplined", w.wrap({})["behavior_pattern"]
  end
end

class TestValidator < Minitest::Test
  def test_nil_invalid
    v = LhStandardAdapter::Validator.new
    assert_equal false, v.validate(nil)[:valid]
  end

  def test_valid_passes
    a = LhStandardAdapter::LongHunAdapter.new
    wrapped = a.wrap({"h"=>"w"})
    r = LhStandardAdapter::Validator.new.validate(wrapped)
    assert_equal true, r[:valid], r[:summary]
  end
end

class TestLongHunAdapter < Minitest::Test
  def test_instance
    a = LhStandardAdapter::LongHunAdapter.new
    assert_equal "9622", a.uid
    assert_equal "HM-9622-001", a.device
  end

  def test_wrap_structure
    a = LhStandardAdapter::LongHunAdapter.new
    r = a.wrap({"code"=>"test"})
    %w[dna audit payload meta].each { |k| assert r.key?(k.to_sym), "missing #{k}" }
  end

  def test_wrap_preserves_payload
    a = LhStandardAdapter::LongHunAdapter.new
    data = {"code"=>"print('hello')"}
    r = a.wrap(data)
    assert_equal data, r[:payload]
  end

  def test_self_validate
    a = LhStandardAdapter::LongHunAdapter.new
    r = a.validate(a.wrap({}))
    assert_equal true, r[:valid], r[:summary]
  end

  def test_get_schemas
    a = LhStandardAdapter::LongHunAdapter.new
    schemas = a.schemas
    assert schemas.key?(:dna_schema)
    assert schemas.key?(:audit_schema)
  end
end
