SELECT
  `date`,
  STRUCT(
    `order`.`group`
  ) AS `order`,
  ARRAY(
    SELECT AS STRUCT
      `select`.`from`
    FROM
      UNNEST(`select`) AS `select`
    WITH
      OFFSET
    ORDER BY
      OFFSET
  ) AS `select`
