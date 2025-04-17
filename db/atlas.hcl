env "dev" {
    url = "postgresql://nlab_owner:npg_rGdyTeIkpQ52@ep-mute-fire-a4rvbeoj-pooler.us-east-1.aws.neon.tech/nlab?sslmode=require"
    migration {
        dir = "file://migrations"
    }
  schemas = ["public"]
}