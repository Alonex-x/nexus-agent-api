# Nexus Ecosystem

The Nexus ecosystem is a modular monitoring and orchestration system for software agents. Each component communicates through a central REST API, enabling registration, mission assignment, event reporting, and real-time supervision.

## Architecture

[Diagrama en formato Mermaid, se renderiza en GitHub]

## Components

| Project | Language | Description |
|----------|----------|-------------|
| [Nexus Terminal](https://github.com/Alonex-x/nexus-terminal) | HTML/CSS/JS | Visual control panel with CRT aesthetics |
| [Nexus Agent Management API](https://github.com/Alonex-x/nexus-agent-api) | Java/Spring Boot | Central REST API for the ecosystem |
| [Nexus Scraper](https://github.com/Alonex-x/nexus-scraper) | Python/Playwright | Stealth web scraping agent |
| [Desktop Automation Toolkit](https://github.com/Alonex-x/desktop-automation-toolkit) | Python | Desktop task automation |
| [Network Traffic Analyzer](https://github.com/Alonex-x/network-traffic-analyzer) | Java | Network traffic analysis |
| [File Integrity Monitor](https://github.com/Alonex-x/file-integrity-monitor) | C++ | File integrity monitoring |

## Workflow

1. Agents register with the API and obtain an API Key.
2. Each agent sends periodic heartbeats to remain ONLINE.
3. The Nexus Terminal queries agent status and displays the panel in real time.
4. Missions are created through the API and picked up by the corresponding agents.
5. Agents report results and events through the API.
