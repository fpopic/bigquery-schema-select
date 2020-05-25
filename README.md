# bigquery-schema-select

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.fpopic/bigquery-schema-select/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/com.github.fpopic/bigquery-schema-select)

Generates SQL query that selects all fields (recursively for nested fields) from the provided BigQuery schema file.

#### Usage

```shell script
git clone git@github.com:fpopic/bigquery-schema-select.git
cd bigquery-schema-select
sbt assembly
```

Using existing table: 

```shell script
bq show --schema --format=prettyjson my_dataset:my_project.my_table | java -jar target/scala-2.13/bigquery-schema-select-assembly-0.1.jar
```

Using JSON schema file:

```shell script
cat src/test/resources/my_schema.json | java -jar target/scala-2.13/bigquery-schema-select-assembly-0.1.jar
```

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
    "name": "R",
    "type": "TIMESTAMP",
    "mode": "REPEATED"
  }
]
```

Would generate:
```sql
SELECT
  A,
  B,
  STRUCT(
    STRUCT(
      C.D.E,
      ARRAY(
        SELECT AS STRUCT
          F.G
        FROM
          UNNEST(C.D.F) AS F
          WITH OFFSET AS offset
        ORDER BY
          offset
      ) AS F
    ) AS D,
    C.H
  ) AS C,
  STRUCT(
    I.J,
    I.K
  ) AS I,
  ARRAY(
    SELECT AS STRUCT
      L.M,
      L.N,
      STRUCT(
        L.O.P
      ) AS O
    FROM
      UNNEST(L) AS L
      WITH OFFSET AS offset
    ORDER BY
      offset
  ) AS L,
  R
```
