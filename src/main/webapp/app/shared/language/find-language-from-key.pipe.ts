import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'findLanguageFromKey' })
export class FindLanguageFromKeyPipe implements PipeTransform {
  private languages: { [key: string]: { name: string; rtl?: boolean } } = {
    'zh-cn': { name: '中文（简体）' },
    cs: { name: 'Český' },
    en: { name: 'English' },
    hi: { name: 'हिंदी' },
    tr: { name: 'Türkçe' },
    ua: { name: 'Українська' },
    // jhipster-needle-i18n-language-key-pipe - JHipster will add/remove languages in this object
  };

  transform(lang: string): string {
    return this.languages[lang].name;
  }
}
