-- UUID 생성용
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--------------------------------------------------
-- users
--------------------------------------------------
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

--------------------------------------------------
-- calendars
--------------------------------------------------
CREATE TABLE calendars (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_user_id UUID NOT NULL,
    timezone VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_calendars_owner
        FOREIGN KEY (owner_user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
);

--------------------------------------------------
-- events
--------------------------------------------------
CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    calendar_id UUID NOT NULL,
    created_by UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    color VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_events_calendar
        FOREIGN KEY (calendar_id)
        REFERENCES calendars(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_events_creator
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_events_status
        CHECK (status IN ('PLANNED', 'CONFIRMED', 'DONE', 'CANCELLED'))
);

--------------------------------------------------
-- event_slots
--------------------------------------------------
CREATE TABLE event_slots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_id UUID NOT NULL,
    calendar_id UUID NOT NULL,

    slot_title VARCHAR(200) NOT NULL,
    slot_date DATE NOT NULL,
    slot_index INT NOT NULL,

    slot_start_at TIMESTAMP NOT NULL,
    slot_end_at TIMESTAMP NOT NULL,

    is_done BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_slots_event
        FOREIGN KEY (event_id)
        REFERENCES events(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_slots_calendar
        FOREIGN KEY (calendar_id)
        REFERENCES calendars(id)
        ON DELETE CASCADE,

    -- 하루 48칸(30분) 기준 겹침 방지
    CONSTRAINT uq_calendar_day_slot
        UNIQUE (calendar_id, slot_date, slot_index)
);

--------------------------------------------------
-- index (조회 성능)
--------------------------------------------------
CREATE INDEX idx_events_calendar_id ON events(calendar_id);
CREATE INDEX idx_slots_event_id ON event_slots(event_id);
CREATE INDEX idx_slots_calendar_date ON event_slots(calendar_id, slot_date);
