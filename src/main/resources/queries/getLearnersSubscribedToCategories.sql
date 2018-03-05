SELECT DISTINCT l.user_id,
                l.email
FROM   (SELECT category_id
        FROM   category_resource_reltn
        WHERE  resource_id = ?) AS resource_categories
       JOIN user_subscription us
         ON resource_categories.category_id = us.category_id
       JOIN learners l
         ON us.user_id = l.user_id; 