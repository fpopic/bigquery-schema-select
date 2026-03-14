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
        WITH
          OFFSET
        ORDER BY
          OFFSET
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
    WITH
      OFFSET
    ORDER BY
      OFFSET
  ) AS L,
  Q
