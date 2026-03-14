### Problem you have encountered:
The BigQuery UI currently defaults to `SELECT *` when a user clicks the "Query" button. While convenient for ad-hoc exploration, `SELECT *` is a significant anti-pattern for production-grade data pipelines and Medallion architectures because it creates an **"Implicit Contract."** 

This leads to **Schema Drift**, where any upstream change (like adding a new, unvetted field) is automatically leaked to downstream consumers, potentially breaking systems or exposing sensitive data. Furthermore, manually rewriting complex SQL for tables with hundreds of fields—especially those with nested `RECORD` and `REPEATED RECORD` types—is redundant, time-consuming, and highly prone to syntax errors.

### What you expected to happen:
I expected the BigQuery Console to provide an **"Explicit Select"** or **"Query with Full Schema"** UI option. This feature would automatically generate a fully specified SQL statement that:
*  **Recursively expands all fields**, including nested `RECORD` (STRUCT) types.
*  **Handles Repeated Records correctly** using the `ARRAY(SELECT AS STRUCT ... FROM UNNEST(...) WITH OFFSET ORDER BY OFFSET)` pattern, which is the only way to maintain a strict schema contract while preserving array order in BigQuery.
*  **Applies Backtick Quoting** to reserved keywords (e.g., `date`, `order`, `group`) to ensure the generated SQL is immediately runnable.
*  **Acts as a Data Contract**, allowing a view to serve as a stable **"Public API"** that shields downstream consumers from underlying raw data changes.

### Steps to reproduce:
* Open the BigQuery Console and navigate to a table with a complex, nested schema (50+ fields, including nested and repeated records).
* Click the "Query" button.
* Observe that it defaults to `SELECT *`, which provides no protection against schema drift.
* Attempt to manually write a fully specified SQL "contract" for this table. Note the excessive manual effort required to correctly handle every `UNNEST` and `STRUCT` mapping.

### Other information (workarounds you have tried, documentation consulted, etc):
I have developed a lightweight, recursive reference implementation of this logic in Bash/jq that successfully addresses these challenges (supporting recursive expansion, backtick quoting, and optional snake_case aliasing):  
**https://github.com/fpopic/bigquery-schema-select**

Currently, the only workaround is for data engineers to maintain custom scripts or manually rewrite these complex queries, which hinders the adoption of data governance best practices. This feature would bring "Schema-Safe" capabilities directly to the UI, significantly improving data reliability and developer productivity across the Google Cloud ecosystem.
