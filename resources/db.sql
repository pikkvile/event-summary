create table event_analytics (
  event_id integer,
  symbol character varying (8),
  hp numeric,
  lp numeric,
  haph numeric,
  hapl numeric,
  laph numeric,
  lapl numeric,
  day integer,
  primary key (event_id, symbol, day)
);
