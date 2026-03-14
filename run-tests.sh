#!/bin/bash

# Test script for bq-schema-select.sh

set -e

echo "Running tests for bq-schema-select.sh..."

echo "Test 1: my_schema.json"
./bq-schema-select.sh < test-resources/my_schema.json | diff - test-resources/my_select.sql
echo "Test 1 passed!"

echo "Test 2: my_camel_schema.json --use_snake_case"
./bq-schema-select.sh --use_snake_case < test-resources/my_camel_schema.json | diff - test-resources/my_camel_select.sql
echo "Test 2 passed!"

echo "Test 3: my_camel_short_schema.json --use_snake_case"
./bq-schema-select.sh --use_snake_case < test-resources/my_camel_short_schema.json | diff - test-resources/my_camel_short_select.sql
echo "Test 3 passed!"

echo "All tests passed!"
