#!/bin/bash

# Test script for bigquery-schema-select

set -e

echo "Running tests for bigquery-schema-select..."

echo "Test 1: my_schema.json"
./bin/bigquery-schema-select < test-resources/my_schema.json | diff -u - test-resources/my_select.sql
echo "Test 1 passed!"

echo "Test 2: my_camel_schema.json --use_snake_case"
./bin/bigquery-schema-select --use_snake_case < test-resources/my_camel_schema.json | diff -u - test-resources/my_camel_select.sql
echo "Test 2 passed!"

echo "Test 3: my_camel_short_schema.json --use_snake_case"
./bin/bigquery-schema-select --use_snake_case < test-resources/my_camel_short_schema.json | diff -u - test-resources/my_camel_short_select.sql
echo "Test 3 passed!"

echo "Test 4: my_reserved_schema.json"
./bin/bigquery-schema-select < test-resources/my_reserved_schema.json | diff -u - test-resources/my_reserved_select.sql
echo "Test 4 passed!"

echo "All tests passed!"
