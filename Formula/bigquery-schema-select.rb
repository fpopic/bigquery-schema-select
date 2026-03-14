class BigquerySchemaSelect < Formula
  desc "Generates SQL query that selects all fields from a BigQuery schema"
  homepage "https://github.com/fpopic/bigquery-schema-select"
  url "https://github.com/fpopic/bigquery-schema-select/archive/refs/tags/v1.2.tar.gz"
  sha256 "beb6e8f7333c33e798a0fac2d37c2c0a69764ddb2689f173768e604a5f363c39"
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
