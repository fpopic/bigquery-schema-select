SELECT
  myPrimitive AS my_primitive,
  STRUCT(
    myStruct.myA AS my_a,
    myStruct.myB AS my_b
  ) AS my_struct,
  STRUCT(
    myStructOfStruct.my1,
    STRUCT(
      myStructOfStruct.my2Struct.my2
    ) AS my2_struct
  ) AS my_struct_of_struct,
  myRepeatedPrimitive AS my_repeated_primitive,
  ARRAY(
    SELECT AS STRUCT
      myRepeatedStruct.myX AS my_x,
      myRepeatedStruct.myY AS my_y
    FROM
      UNNEST(myRepeatedStruct) AS myRepeatedStruct
    WITH
      OFFSET
    ORDER BY
      OFFSET
  ) AS my_repeated_struct,
  nochange
