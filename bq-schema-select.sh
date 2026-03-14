#!/bin/bash

# Ported from Scala to Bash + jq

USE_SNAKE_CASE=false
if [[ "$1" == "--use_snake_case" ]]; then
  USE_SNAKE_CASE=true
fi

jq -r --argjson useSnakeCase "$USE_SNAKE_CASE" '
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
