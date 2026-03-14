# bigquery-schema-select

Generates SQL query that selects all fields (recursively for nested fields) from the provided BigQuery schema file.

### Prerequisites

- `jq` installed on your system.
- `bash` shell.

### Usage

Using existing table: 

```shell script
bq show --schema --format=prettyjson my_project:my_dataset.my_table | ./bq-schema-select.sh
```

Using JSON schema file:

```shell script
cat my_schema.json | ./bq-schema-select.sh
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
      }
    ]
  }
]
```

Generates:
```sql
SELECT
  A,
  STRUCT(
    STRUCT(
      C.D.E,
      ARRAY(
        SELECT AS STRUCT
          F.G
        FROM
          UNNEST(C.D.F) AS F
        WITH
          OFFSET
        ORDER BY
          OFFSET
      ) AS F
    ) AS D
  ) AS C
```

In case you would like to use snake_case for field names use flag `--use_snake_case`:
```shell script
cat my_schema.json | ./bq-schema-select.sh --use_snake_case
```

### Development

Run tests:
```shell script
./run-tests.sh
```
