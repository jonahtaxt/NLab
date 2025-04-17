# NLab

Install brew MacOS
  /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
  echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
  eval "$(/opt/homebrew/bin/brew shellenv)"

Install Atlas CLI (for PostgreSQL publish)
  brew install ariga/tap/atlas

Update schema
  atlas migrate diff "add created_at column to users" \
  --env local \
  --to file://schema

Apply schema changes
  atlas migrate apply --env local

Next.js
    pnpm install
    pnpm run dev

vscode launch.json
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "AppointmentapiApplication",
            "request": "launch",
            "mainClass": "com.effisoft.nlab.appointmentapi.AppointmentapiApplication",
            "projectName": "nlab"
        },
        {
            "name": "Launch Next.js App (pnpm)",
            "type": "node",
            "request": "launch",
            "cwd": "${workspaceFolder}/web",
            "runtimeExecutable": "pnpm",
            "runtimeArgs": ["run", "dev"],
            "console": "integratedTerminal",
            "internalConsoleOptions": "neverOpen",
            "skipFiles": ["<node_internals>/**"]
          },
          {
            "name": "Attach to Arc (Next.js)",
            "type": "pwa-chrome",
            "request": "launch",
            "url": "http://localhost:3000",
            "webRoot": "${workspaceFolder}/web",
            "runtimeExecutable": "/Applications/Arc.app/Contents/MacOS/Arc",
            "runtimeArgs": ["--remote-debugging-port=9222"],
            "port": 9222
          }
    ],
    "compounds": [
      {
        "name": "Start API, Dev + Attach to Arc",
        "configurations": [
          "AppointmentapiApplication",
          "Launch Next.js App (pnpm)",
          "Attach to Arc (Next.js)"
        ]
      }
    ]
}