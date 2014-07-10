CREATE TABLE if not exists LAT_LONG
  (
    LAT_LONG_ID            bigint NOT NULL primary key auto_increment,
    specimenid             bigint NOT NULL ,
    LAT_DEG                int,
    DEC_LAT_MIN            DECIMAL(8,6),
    LAT_MIN                int,
    LAT_SEC                DECIMAL(8,6),
    LAT_DIR                VARCHAR(1),
    LONG_DEG               int,
    DEC_LONG_MIN           DECIMAL(10,8),
    LONG_MIN               int,
    LONG_SEC               DECIMAL(8,6),
    LONG_DIR               VARCHAR(1),
    DEC_LAT                DECIMAL(12,10),
    DEC_LONG               DECIMAL(13,10),
    DATUM                  VARCHAR(55) NOT NULL ,
    UTM_ZONE               VARCHAR(3),
    UTM_EW                 int,
    UTM_NS                 int,
    ORIG_LAT_LONG_UNITS    VARCHAR(20) NOT NULL ,
    DETERMINED_BY_AGENT    VARCHAR(255) ,
    DETERMINED_DATE        DATE NOT NULL ,
    LAT_LONG_REF_SOURCE  VARCHAR(500) NOT NULL ,
    LAT_LONG_REMARKS     VARCHAR(4000),
    MAX_ERROR_DISTANCE   int,
    MAX_ERROR_UNITS      VARCHAR(2),
    NEAREST_NAMED_PLACE  VARCHAR(255),
    LAT_LONG_FOR_NNP_FG  int,
    FIELD_VERIFIED_FG    int,
    ACCEPTED_LAT_LONG_FG int NOT NULL ,
    EXTENT               DECIMAL(12,5),
    GPSACCURACY          DECIMAL(8,3),
    GEOREFMETHOD         VARCHAR(255) NOT NULL ,
    VERIFICATIONSTATUS   VARCHAR(40) NOT NULL ,
    CONSTRAINT ACCEPTED_LAT_LONG_FG_RANGE CHECK (ACCEPTED_LAT_LONG_FG IN (0,1)) ,
    CONSTRAINT DEC_LAT_RANGE CHECK (dec_lat BETWEEN   -90 AND 90) ,
    CONSTRAINT DEC_LONG_RANGE CHECK (dec_long BETWEEN -180 AND 180) ,
    CONSTRAINT DEC_LAT_MIN_RANGE CHECK (DEC_LAT_MIN >= 0 AND DEC_LAT_MIN < 60) ,
    CONSTRAINT LONG_DEG_RANGE CHECK (LONG_DEG BETWEEN 0 AND 180) ,
    CONSTRAINT DEC_LONG_MIN_RANGE CHECK (DEC_LONG_MIN >= 0 AND DEC_LONG_MIN < 60) ,
    CONSTRAINT FK_LATLONG_specimen FOREIGN KEY (specimenid) REFERENCES SPECIMEN (specimenid)
  ) ;
    CREATE UNIQUE INDEX FK_specimen_ID ON LAT_LONG ( specimenid) ;

    CREATE INDEX LAT_LONG_DEC_ALL ON LAT_LONG ( DEC_LAT, DEC_LONG) ;

CREATE OR REPLACE TRIGGER LAT_LONG_CT_CHECK BEFORE
  UPDATE OR
  INSERT ON LAT_LONG FOR EACH ROW DECLARE numrows int;
  BEGIN
    SELECT COUNT(*)
    INTO numrows
    FROM ctVERIFICATIONSTATUS
    WHERE VERIFICATIONSTATUS = :NEW.VERIFICATIONSTATUS;
    IF (numrows              = 0) THEN
      raise_application_error( -20001, 'Invalid VERIFICATIONSTATUS');
    END IF;
    SELECT COUNT(*)
    INTO numrows
    FROM ctGEOREFMETHOD
    WHERE GEOREFMETHOD = :NEW.GEOREFMETHOD;
    IF (numrows        = 0) THEN
      raise_application_error( -20001, 'Invalid GEOREFMETHOD');
    END IF;
    SELECT COUNT(*) INTO numrows FROM ctdatum WHERE datum = :NEW.datum;
    IF (numrows = 0) THEN
      raise_application_error( -20001, 'Invalid datum');
    END IF;
    SELECT COUNT(*)
    INTO numrows
    FROM ctlat_long_units
    WHERE orig_lat_long_units = :NEW.orig_lat_long_units;
    IF (numrows               = 0) THEN
      raise_application_error( -20001, 'Invalid orig_lat_long_units');
    END IF;
    IF (:NEW.MAX_ERROR_UNITS IS NOT NULL) THEN
      SELECT COUNT(*)
      INTO numrows
      FROM ctlat_long_error_units
      WHERE LAT_LONG_ERROR_UNITS = :NEW.MAX_ERROR_UNITS;
      IF (numrows                = 0) THEN
        raise_application_error( -20001, 'Invalid MAX_ERROR_UNITS');
      END IF;
    END IF;
    IF (:NEW.orig_lat_long_units = 'decimal degrees') THEN
      IF (:NEW.dec_lat          IS NULL OR :NEW.dec_long IS NULL) THEN
        raise_application_error( -20001, 'dec_lat and dec_long are required when orig_lat_long_units is decimal degrees');
      END IF;
    ELSIF (:NEW.orig_lat_long_units = 'deg. min. sec.') THEN
      IF (:NEW.LAT_DEG             IS NULL OR :NEW.LAT_DIR IS NULL OR :NEW.LONG_DEG IS NULL OR :NEW.LONG_DIR IS NULL ) THEN
        raise_application_error( -20001, 'Insufficient information to create new coordinates with degrees minutes seconds');
      END IF;
    ELSIF (:NEW.orig_lat_long_units = 'degrees dec. minutes') THEN
      IF (:NEW.LAT_DEG             IS NULL OR :NEW.LAT_DIR IS NULL OR :NEW.LONG_DEG IS NULL OR :NEW.LONG_DIR IS NULL ) THEN
        raise_application_error( -20001, 'Insufficient information to create new coordinates with degrees dec. minutes');
      END IF;
    ELSIF (:NEW.orig_lat_long_units = 'UTM') THEN
      IF (:NEW.utm_ew              IS NULL OR :NEW.utm_ns IS NULL OR :NEW.utm_zone IS NULL) THEN
        raise_application_error( -20001, 'Insufficient information to create new coordinates with UTM');
      END IF;
    ELSE
      raise_application_error( -20001, :NEW.orig_lat_long_units || ' is not handled. Please contact your database administrator.' );
    END IF;
  END;
  /
  ALTER TRIGGER LAT_LONG_CT_CHECK ENABLE;

CREATE OR REPLACE TRIGGER UPDATECOORDINATES
  -- Trigger to calculate decimal degrees from other formats when data are changed.
  -- DLM 6Dec04
  BEFORE
  UPDATE OR
  INSERT ON LAT_LONG FOR EACH ROW BEGIN IF :new.orig_lat_long_units = 'deg. min. sec.' THEN :new.dec_lat := :new.lat_deg +
    (
      :new.lat_min / 60
    )
    +
    (
      NVL(:new.lat_sec,0) / 3600
    );
  IF :new.lat_dir = 'S' THEN
    :new.dec_lat := :new.dec_lat * -1;
  END IF;
  :new.dec_long := :new.long_deg +
  (
    :new.long_min / 60
  )
  +
  (
    NVL(:new.long_sec,0) / 3600
  )
  ;
  IF :new.long_dir = 'W' THEN
    :new.dec_long := :new.dec_long * -1;
  END IF;
ELSIF :new.orig_lat_long_units = 'degrees dec. minutes' THEN
  :new.dec_lat                := :new.lat_deg +
  (
    :new.dec_lat_min / 60
  )
  ;
  IF :new.lat_dir = 'S' THEN
    :new.dec_lat := :new.dec_lat * -1;
  END IF;
  :new.dec_long := :new.long_deg +
  (
    :new.dec_long_min / 60
  )
  ;
  IF :new.long_dir = 'W' THEN
    :new.dec_long := :new.dec_long * -1;
  END IF;
END IF;
END updateCoordinates;
/
ALTER TRIGGER UPDATECOORDINATES ENABLE;
