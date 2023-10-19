DROP TABLE IF EXISTS "transaction";

CREATE TABLE "transaction" (
                               "transaction_id" uuid DEFAULT random_uuid(),
                               "description" VARCHAR(50) NOT NULL,
                               "transaction_date" DATE NOT NULL,
                               "amount" NUMERIC NOT NULL,
                               PRIMARY KEY ("transaction_id")
)