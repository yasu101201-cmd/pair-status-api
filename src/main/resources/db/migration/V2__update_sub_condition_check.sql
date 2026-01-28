ALTER TABLE condition_updates
DROP CONSTRAINT IF EXISTS condition_updates_sub_condition_check;

ALTER TABLE condition_updates
ADD CONSTRAINT condition_updates_sub_condition_check
CHECK (
  sub_condition IS NULL OR sub_condition IN (
    'LONELY', 'PAINFUL', 'HAPPY',
    'HUNGRY', 'TIRED', 'SLEEPY',
    'COLD', 'FEVER', 'HEADACHE', 'SLUGGISH'
  )
);