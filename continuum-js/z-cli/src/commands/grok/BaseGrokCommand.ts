// src/commands/grok/BaseGrokCommand.ts
import { Command } from '@oclif/core'
import chalk from 'chalk'
import puppeteer from 'puppeteer-extra'
import StealthPlugin from 'puppeteer-extra-plugin-stealth'
import { loadGrokConfig } from '../../internal/state/ConfigGrok.js'
import { Cookie, Page } from 'puppeteer'

puppeteer.use(StealthPlugin())

export abstract class BaseGrokCommand extends Command {
    // @ts-ignore
    protected browser: puppeteer.Browser | null = null

    protected async setupBrowser(url: string, responseHandler?: (page: Page) => void, headless: boolean = false): Promise<Page> {
        const config = await loadGrokConfig(this.config.configDir)

        if (!config.grokCookie) {
            this.log(chalk.red('No Grok cookie configured. Run `z grok:config` first.'))
            throw new Error('No cookie configured')
        }

        this.browser = await puppeteer.launch({
                                                  headless: headless ? 'shell' : false, // true for new headless mode, false for visible
                                                  args: [
                                                      '--no-sandbox',
                                                      '--disable-setuid-sandbox',
                                                  ],
                                              })

        process.on('SIGINT', async () => {
            this.log(chalk.blue('Session interrupted. Cleaning up...'))
            if (this.browser) await this.browser.close()
            process.exit(0)
        })

        const context = this.browser.defaultBrowserContext()
        const page = await context.newPage()

        const cookieEntries = config.grokCookie.split(/;\s*|\s+/)
        const cookies = cookieEntries.map((cookieStr) => {
            const [name, ...valueParts] = cookieStr.split('=')
            const value = valueParts.join('=')
            if (!name || !value) return null
            return {
                name: name.trim(),
                value: value.trim(),
                domain: 'grok.com',
                path: '/',
                httpOnly: true,
                secure: true
            }
        }).filter(cookie => cookie !== null) as { name: string, value: string, domain: string, path: string, httpOnly: boolean, secure: boolean }[]

        if (cookies.length === 0) {
            this.log(chalk.red('No valid cookies parsed. Check your cookie string.'))
            throw new Error('No valid cookies')
        }

        await context.setCookie(...cookies)

        if (responseHandler) {
            responseHandler(page)
        }

        await page.goto(url, { waitUntil: 'networkidle2' })

        const isCaptchaPresent = await page.evaluate(() =>
                                                         !!document.querySelector('input[name="cf-turnstile-response"]')
        )

        if (isCaptchaPresent) {
            this.log(chalk.yellow('Cloudflare CAPTCHA detected. Please solve it in the browser window...'))
            await page.waitForFunction(
                () => !document.querySelector('input[name="cf-turnstile-response"]'),
                { timeout: 60000 }
            )
            this.log(chalk.green('CAPTCHA solved or bypassed. Proceeding...'))
        }

        const updatedCookies = await page.cookies()
        const cfClearance = updatedCookies.find((c: Cookie) => c.name === 'cf_clearance')
        if (!cfClearance) {
            this.log(chalk.red('Cloudflare clearance not obtained.'))
            const content = await page.content()
            this.log(chalk.yellow('Page content:'), content)
            throw new Error('Cloudflare clearance not obtained')
        }

        return page
    }

    protected async cleanup(): Promise<void> {
        if (this.browser) await this.browser.close()
    }
}