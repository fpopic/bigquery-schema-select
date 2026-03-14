class BigquerySchemaSelect < Formula
  desc "Generates SQL query that selects all fields from a BigQuery schema"
  homepage "https://github.com/fpopic/bigquery-schema-select"
  url "https://github.com/fpopic/bigquery-schema-select/archive/refs/tags/v1.3.tar.gz"
  sha256 "241da3b4f505334b64f231c98aeb61974c3100077e2db78d8a60da2856b8c4a4"
  license "Apache-2.0"

  depends_on "jq"

  def install
    bin.install "bin/bigquery-schema-select"
  end

  test do
    (testpath/"schema.json").write <<~EOS
      [{"name": "test_field", "type": "STRING"}]
    EOS
    output = shell_output("cat #{testpath}/schema.json | #{bin}/bigquery-schema-select")
    assert_match "SELECT", output
    assert_match "test_field", output
  end
end
