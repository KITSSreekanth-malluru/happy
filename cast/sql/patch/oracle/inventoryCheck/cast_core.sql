CREATE TABLE inventory_request (
        request_id VARCHAR(32) not null,
        request_date DATE not null,
        store_id VARCHAR(32) not null,
        order_id VARCHAR(32) not null,
        request_status VARCHAR(32) not null,
        primary key(request_id)
);

CREATE TABLE inventory_request_order_items (
        request_id VARCHAR(32) not null references inventory_request(request_id),
        order_item_id VARCHAR(32) not null,
        primary key(request_id, order_item_id)
);

commit;