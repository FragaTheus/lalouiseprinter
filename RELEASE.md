#  LaLouise — Guia de Releases

> Versionamento semântico, tags Git e publicação de releases com GitHub CLI (`gh`)

---

##  Sumário

- [Convenção de Versionamento](#convenção-de-versionamento)
- [Estratégia de Branches](#estratégia-de-branches)
- [Fluxo de Release](#fluxo-de-release)
- [Comandos gh — Releases Backend (lalouise)](#comandos-gh--releases-backend-lalouise)
- [Comandos gh — Releases Frontend (ui)](#comandos-gh--releases-frontend-ui)
- [Comandos gh — Releases Print Agent (printer)](#comandos-gh--releases-print-agent-printer)
- [CI/CD com GitHub Actions](#cicd-com-github-actions)
- [Checklist de Release](#checklist-de-release)
- [Histórico de Releases](#histórico-de-releases)

---

## Convenção de Versionamento

O projeto segue **Semantic Versioning 2.0.0** (`MAJOR.MINOR.PATCH`):

| Tipo | Quando usar | Exemplo |
|---|---|---|
| `MAJOR` | Mudanças incompatíveis de API ou arquitetura | `2.0.0` |
| `MINOR` | Novas funcionalidades retrocompatíveis | `1.3.0` |
| `PATCH` | Correções de bugs sem quebra de contrato | `1.2.5` |

### Prefixos de Tag por Componente

Como o repositório é monorepo, as tags são prefixadas por componente:

| Componente | Prefixo | Exemplo |
|---|---|---|
| Backend API | `api/` | `api/v1.2.0` |
| Frontend | `ui/` | `ui/v1.1.3` |
| Print Agent | `printer/` | `printer/v1.0.1` |
| Release global | `v` | `v1.2.0` |

---

## Estratégia de Branches

```
main              ← Produção (protegida)
  └── develop     ← Integração contínua
        ├── feature/nome-da-feature
        ├── fix/nome-do-bug
        └── hotfix/nome-do-hotfix
```

- **`main`**: Sempre estável, cada merge gera uma tag de release
- **`develop`**: Branch de integração, recebe PRs de features e fixes
- **`feature/*`**: Novas funcionalidades (branch do `develop`, PR para `develop`)
- **`fix/*`**: Correções não urgentes (branch do `develop`)
- **`hotfix/*`**: Correções urgentes em produção (branch do `main`, PR para `main` e `develop`)

---

## Fluxo de Release

### Release Normal

```bash
# 1. Garanta que develop está atualizado e os testes passam
git checkout develop
git pull origin develop

# 2. Merge para main via PR ou diretamente
git checkout main
git merge develop --no-ff -m "chore: merge develop into main for release v1.2.0"

# 3. Crie a tag anotada
git tag -a api/v1.2.0 -m "release: api v1.2.0 — descrição das mudanças"

# 4. Push da tag
git push origin main
git push origin api/v1.2.0

# 5. Crie o release no GitHub (veja comandos abaixo)
```

### Hotfix

```bash
# 1. Branch do main
git checkout main
git checkout -b hotfix/correcao-critica

# 2. Faça a correção e commit
git commit -m "fix: corrige bug crítico em X"

# 3. Merge para main e develop
git checkout main && git merge hotfix/correcao-critica --no-ff
git checkout develop && git merge hotfix/correcao-critica --no-ff

# 4. Tag e release
git tag -a api/v1.2.1 -m "hotfix: api v1.2.1 — corrige bug crítico em X"
git push origin main develop api/v1.2.1
```

---

## Comandos gh — Releases Backend (lalouise)

```bash
# Build do artefato antes do release
cd lalouise
./gradlew clean bootJar

# Criar release com upload do JAR
gh release create api/v1.2.0 \
  ./build/libs/lalouise-0.0.1-SNAPSHOT.jar \
  --title "LaLouise API v1.2.0" \
  --notes "## O que há de novo

### ✨ Novas Funcionalidades
- Descrição da feature 1
- Descrição da feature 2

###  Correções
- Correção do bug X

###  Segurança
- Atualização de dependências

### ⚠️ Breaking Changes
- Nenhuma" \
  --target main

# Release como pre-release (beta/RC)
gh release create api/v1.3.0-rc.1 \
  ./build/libs/lalouise-0.0.1-SNAPSHOT.jar \
  --title "LaLouise API v1.3.0-rc.1" \
  --notes "Release candidate 1 para v1.3.0" \
  --prerelease \
  --target develop

# Listar releases existentes
gh release list

# Ver detalhes de um release
gh release view api/v1.2.0

# Editar release
gh release edit api/v1.2.0 --notes "Notas atualizadas"

# Deletar release (mantém a tag)
gh release delete api/v1.2.0

# Deletar tag
git push --delete origin api/v1.2.0
git tag -d api/v1.2.0
```

---

## Comandos gh — Releases Frontend (ui)

```bash
# Build do frontend
cd ui
pnpm install
pnpm build

# O deploy do frontend é feito automaticamente pela Vercel
# O release no GitHub serve como marcador de versão

gh release create ui/v1.1.0 \
  --title "LaLouise UI v1.1.0" \
  --notes "## Interface v1.1.0

### ✨ Novas Funcionalidades
- Nova tela de dashboard
- Filtros avançados de etiquetas

###  Correções
- Correção de layout em mobile

###  Melhorias de UX
- Redesign do fluxo de emissão de etiquetas" \
  --target main

# Pre-release de UI
gh release create ui/v1.2.0-beta.1 \
  --title "LaLouise UI v1.2.0-beta.1" \
  --notes "Beta para testes internos" \
  --prerelease \
  --target develop
```

---

## Comandos gh — Releases Print Agent (printer)

```bash
# Build do Print Agent
cd printer
./gradlew clean bootJar

# Criar release com upload do JAR (distribuído para as unidades)
gh release create printer/v1.0.0 \
  ./build/libs/lalouise-print-agent.jar \
  --title "LaLouise Print Agent v1.0.0" \
  --notes "## Print Agent v1.0.0

### ️ Notas de instalação
1. Baixe o JAR anexo neste release
2. Siga o guia em printer/README.md para instalação como Windows Service
3. Configure restaurant.id e printer.name antes de instalar

### ✨ Funcionalidades
- Consumo assíncrono de jobs via RabbitMQ
- Envio ZPL para impressoras Zebra
- Retry automático em erros transitórios
- Rejeição permanente em falhas físicas de impressora

###  Configuração obrigatória
\`\`\`properties
restaurant.id=<UUID-DO-RESTAURANTE>
printer.name=<NOME-DA-IMPRESSORA>
spring.rabbitmq.addresses=amqps://...
\`\`\`" \
  --target main
```

---

## CI/CD com GitHub Actions

O projeto utiliza **GitHub Actions** para automatizar build, testes e deploy:

### Fluxo Automático

```
Push em main
    │
    ├── Backend: Gradle build + test → Docker build → Push para registry → Deploy VPS
    │
    ├── Frontend: pnpm build → Deploy automático Vercel
    │
    └── Print Agent: Gradle bootJar → Anexar JAR ao release (quando tag printer/*)
```

### Workflow de Release Automático

Ao criar uma tag `api/v*`, o GitHub Actions executa automaticamente:
1. Build e testes do backend
2. Geração do JAR
3. Build da imagem Docker
4. Push para o registry
5. Deploy na VPS via SSH

Para o frontend, a Vercel detecta o push na `main` e faz o deploy automaticamente.

### Trigger Manual de Deploy

```bash
# Disparar workflow manualmente via gh
gh workflow run deploy-backend.yml --ref main

# Verificar execução de workflows
gh run list

# Ver logs de uma execução
gh run view <run-id> --log
```

---

## Checklist de Release

Antes de criar um release de produção, verifique:

### Backend (api/)
- [ ] Todos os testes passando (`./gradlew test`)
- [ ] Migrações Flyway validadas em staging
- [ ] Variáveis de ambiente de produção atualizadas
- [ ] `docker compose` testado com o novo JAR
- [ ] Endpoints críticos testados manualmente em staging
- [ ] Sem credenciais hardcoded no código

### Frontend (ui/)
- [ ] Build sem erros (`pnpm build`)
- [ ] Sem erros de lint (`pnpm lint`)
- [ ] Testado em staging (Vercel Preview)
- [ ] Variáveis de ambiente da Vercel atualizadas

### Print Agent (printer/)
- [ ] Build sem erros (`./gradlew bootJar`)
- [ ] Testado com RabbitMQ de staging
- [ ] JAR testado em máquina Windows com impressora Zebra
- [ ] README de instalação atualizado se houver mudanças de configuração
- [ ] Comunicado enviado às unidades sobre atualização

---

## Histórico de Releases

| Tag | Componente | Data | Descrição |
|---|---|---|---|
| `api/v0.0.1-SNAPSHOT` | Backend | — | Versão inicial de desenvolvimento |
| `ui/v0.1.0` | Frontend | — | Versão inicial de desenvolvimento |
| `printer/v0.0.1-SNAPSHOT` | Print Agent | — | Versão inicial de desenvolvimento |

> Atualize esta tabela a cada release de produção.

---

<div align="center">
  <sub>LaLouise · Releases e Versionamento · Semantic Versioning 2.0.0</sub>
</div>
