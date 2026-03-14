class BigquerySchemaSelect < Formula
  desc "Generates SQL query that selects all fields from a BigQuery schema"
  homepage "https://github.com/fpopic/bigquery-schema-select"
  url "https://github.com/fpopic/bigquery-schema-select/archive/refs/tags/v1.4.tar.gz"
  sha256 "f2d08f1ad5384e33459b08f147552fccc5ce06057dc33c93bddd6227bf82449d"
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
