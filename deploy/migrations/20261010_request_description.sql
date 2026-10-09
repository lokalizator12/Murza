-- Match the 3000-character request limit; Hibernate update does not widen existing VARCHAR columns.
ALTER TABLE parcel_requests MODIFY COLUMN description VARCHAR(3000) NULL;
ALTER TABLE trip_requests MODIFY COLUMN description VARCHAR(3000) NULL;
