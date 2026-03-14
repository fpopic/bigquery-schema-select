#!/bin/bash

# Ported from Scala to Bash + jq

# Check for dependencies
if ! command -v jq >/dev/null 2>&1; then
  echo "Error: jq is not installed. Please install it (e.g., 'brew install jq' or 'sudo apt install jq')." >&2
  exit 1
fi

# Check for input (stdin)
if [[ -t 0 ]]; then
  echo "Usage: cat schema.json | $0 [--use_snake_case]" >&2
  echo "Or: bq show --schema --format=prettyjson project:dataset.table | $0" >&2
  exit 1
fi

# Read input once
INPUT=$(cat)

# Validate JSON input
if ! echo "$INPUT" | jq -e . >/dev/null 2>&1; then
  echo "Error: Input is not valid JSON." >&2
  if echo "$INPUT" | grep -iq "BigQuery error"; then
    echo "The input contains a BigQuery error. Please ensure your table reference follows the 'project:dataset.table' format (with a colon after the project ID)." >&2
  fi
  exit 1
fi

USE_SNAKE_CASE=false
if [[ "$1" == "--use_snake_case" ]]; then
  USE_SNAKE_CASE=true
fi

echo "$INPUT" | jq -r --argjson useSnakeCase "$USE_SNAKE_CASE" '
  def calculateFieldName(name; useSnakeCase):
    if (useSnakeCase | not) then name
    else
      (name | split("") | .[0] |= ascii_downcase | .[0] as $first | .[1:] | 
       reduce .[] as $char (""; . + (if ($char | test("[A-Z]")) then "_" + ($char | ascii_downcase) else $char end))
       | $first + .)
    end;

  def toSelectClauseRecursive(current; depth; prefix; useSnakeCase):
    (if (prefix == null) then current.name else prefix + "." + current.name end) as $fullyQualifiedName |
    ("  " * depth) as $indent |
    calculateFieldName(current.name; useSnakeCase) as $calculatedFieldName |
    (if (useSnakeCase and $calculatedFieldName != current.name) then " AS " + $calculatedFieldName else "" end) as $alias |
    
    if (current.type != "RECORD") then
      $indent + $fullyQualifiedName + $alias
    elif (current.type == "RECORD" and current.mode == "REPEATED") then
      current.name as $currentName |
      ($indent + "ARRAY(\n" +
       $indent + "  SELECT AS STRUCT\n" +
       (current.fields | map(toSelectClauseRecursive(.; depth + 2; $currentName; useSnakeCase)) | join(",\n")) + "\n" +
       $indent + "  FROM\n" +
       $indent + "    UNNEST(" + $fullyQualifiedName + ") AS " + $currentName + "\n" +
       $indent + "  WITH\n" +
       $indent + "    OFFSET\n" +
       $indent + "  ORDER BY\n" +
       $indent + "    OFFSET\n" +
       $indent + ") AS " + $calculatedFieldName)
    else
      ($indent + "STRUCT(\n" +
       (current.fields | map(toSelectClauseRecursive(.; depth + 1; $fullyQualifiedName; useSnakeCase)) | join(",\n")) + "\n" +
       $indent + ") AS " + $calculatedFieldName)
    end;

  "SELECT\n" + (map(toSelectClauseRecursive(.; 1; null; $useSnakeCase)) | join(",\n"))
'
