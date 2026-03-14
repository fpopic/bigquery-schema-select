# bigquery-schema-select

Generates SQL query that selects all fields (recursively for nested fields) from the provided BigQuery schema file.

### Motivation

This tool is designed to help automate the creation of **explicit BigQuery views** that act as a strict schema "contract" for different layers of a medallion architecture (L0, L1, L2).

By generating an explicit `SELECT` statement that recursively expands `RECORD` and `REPEATED RECORD` types, it ensures that your views:
- **Prevent Schema Drift**: New fields added to the underlying source table will not be exposed in the view until you explicitly update the schema and regenerate it (avoiding the pitfalls of `SELECT *`).
- **Maintain Structure**: Uses `STRUCT(...)` and `ARRAY(SELECT AS STRUCT ...)` to fully specify the output record structure and maintain array order using `WITH OFFSET`.
- **Enforce Naming Standards**: Optionally aliases camelCase fields to snake_case (using the `--use_snake_case` flag) to maintain a consistent public API across your data layers.
- **Automate Redundancy**: Avoids the error-prone and tedious process of manually rewriting complex nested SQL for dozens or hundreds of fields.

### Prerequisites

- `jq` installed on your system.
- `bash` shell.

### Usage

Using existing table: 

```shell script
bq show --schema --format=prettyjson my_project:my_dataset.my_table | ./bin/bigquery-schema-select
```

Using JSON schema file:

```shell script
cat my_schema.json | ./bin/bigquery-schema-select
```

#### Example

Input `my_schema.json`:
```json
[
  {
    "name": "A",
    "type": "TIMESTAMP"
  },
  {
    "name": "B",
    "type": "TIMESTAMP"
  },
  {
    "name": "C",
    "type": "RECORD",
    "fields": [
      {
        "name": "D",
        "type": "RECORD",
        "fields": [
          {
            "name": "E",
            "type": "TIMESTAMP"
          },
          {
            "name": "F",
            "type": "RECORD",
            "mode": "REPEATED",
            "fields": [
              {
                "name": "G",
                "type": "STRING"
              }
            ]
          }
        ]
      },
      {
        "name": "H",
        "type": "TIMESTAMP"
      }
    ]
  },
  {
    "name": "I",
    "type": "RECORD",
    "fields": [
      {
        "name": "J",
        "type": "TIMESTAMP"
      },
      {
        "name": "K",
        "type": "TIMESTAMP"
      }
    ]
  },
  {
    "name": "L",
    "type": "RECORD",
    "mode": "REPEATED",
    "fields": [
      {
        "name": "M",
        "type": "TIMESTAMP"
      },
      {
        "name": "N",
        "type": "TIMESTAMP"
      },
      {
        "name": "O",
        "type": "RECORD",
        "fields": [
          {
            "name": "P",
            "type": "TIMESTAMP"
          }
        ]
      }
    ]
  },
  {
    "name": "Q",
    "type": "TIMESTAMP",
    "mode": "REPEATED"
  }
]
```

Generates:
```sql
SELECT
  `A`,
  `B`,
  STRUCT(
    STRUCT(
      `C`.`D`.`E`,
      ARRAY(
        SELECT AS STRUCT
          `F`.`G`
        FROM
          UNNEST(`C`.`D`.`F`) AS `F`
        WITH
          OFFSET
        ORDER BY
          OFFSET
      ) AS `F`
    ) AS `D`,
    `C`.`H`
  ) AS `C`,
  STRUCT(
    `I`.`J`,
    `I`.`K`
  ) AS `I`,
  ARRAY(
    SELECT AS STRUCT
      `L`.`M`,
      `L`.`N`,
      STRUCT(
        `L`.`O`.`P`
      ) AS `O`
    FROM
      UNNEST(`L`) AS `L`
    WITH
      OFFSET
    ORDER BY
      OFFSET
  ) AS `L`,
  `Q`
```

In case you would like to use snake_case for field names use flag `--use_snake_case`:
```shell script
cat my_schema.json | ./bin/bigquery-schema-select --use_snake_case
```

### Development

Run tests:
```shell script
./run-tests.sh
```
