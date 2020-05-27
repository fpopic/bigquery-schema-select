# bigquery-schema-select

![Scala CI](https://github.com/fpopic/bigquery-schema-select/workflows/Scala%20CI/badge.svg) 
[<img src="https://img.shields.io/maven-central/v/com.github.fpopic/bigquery-schema-select_2.13.svg?color=brightgreen&label=maven%20central%202.13"/>](https://search.maven.org/#search%7Cga%7C1%7Cbigquery-schema-select_2.13)

Generates SQL query that selects all fields (recursively for nested fields) from the provided BigQuery schema file.

### Installation

Download latest version `bigquery-schema-select_2.13-X.Y.jar` from [maven releases UI](https://repo1.maven.org/maven2/com/github/fpopic/bigquery-schema-select_2.13/) or using CLI:

```shell script
# replace X.Y with the latest version
wget -O ~/bigquery-schema-select_2.13-X.Y.jar https://repo1.maven.org/maven2/com/github/fpopic/bigquery-schema-select_2.13/X.Y/bigquery-schema-select_2.13-X.Y.jar
```

### Usage

Using existing table: 

```shell script
bq show --schema --format=prettyjson my_dataset:my_project.my_table | java -jar bigquery-schema-select_2.13-X.Y.jar
```

Using JSON schema file:

```shell script
cat my_schema.json | java -jar bigquery-schema-select_2.13-X.Y.jar
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
