SELECT
  AField1 AS a_field1,
  BField2 AS b_field2,
  STRUCT(
    STRUCT(
      CField3.DField4.EField5 AS e_field5,
      ARRAY(
        SELECT AS STRUCT
          FField6.GField7 AS g_field7
        FROM
          UNNEST(CField3.DField4.FField6) AS FField6
        WITH
          OFFSET
        ORDER BY
          OFFSET
      ) AS f_field6
    ) AS d_field4,
    CField3.HField8 AS h_field8
  ) AS c_field3,
  STRUCT(
    IField9.JField10 AS j_field10,
    IField9.KField11 AS k_field11
  ) AS i_field9,
  ARRAY(
    SELECT AS STRUCT
      LField12.MField13 AS m_field13,
      LField12.NField14 AS n_field14,
      STRUCT(
        LField12.OField15.PField16 AS p_field16
      ) AS o_field15
    FROM
      UNNEST(LField12) AS LField12
    WITH
      OFFSET
    ORDER BY
      OFFSET
  ) AS l_field12,
  QField17 AS q_field17
