import bagShopping from '../assets/icons/bag-shopping.svg'
import bars from '../assets/icons/bars.svg'
import bicycle from '../assets/icons/bicycle.svg'
import bolt from '../assets/icons/bolt.svg'
import book from '../assets/icons/book.svg'
import briefcase from '../assets/icons/briefcase.svg'
import camera from '../assets/icons/camera.svg'
import car from '../assets/icons/car.svg'
import chartSimple from '../assets/icons/chart-simple.svg'
import chevronDown from '../assets/icons/chevron-down.svg'
import circleQuestion from '../assets/icons/circle-question.svg'
import clipboard from '../assets/icons/clipboard.svg'
import comment from '../assets/icons/comment.svg'
import gamepad from '../assets/icons/gamepad.svg'
import gear from '../assets/icons/gear.svg'
import gift from '../assets/icons/gift.svg'
import handHoldingDollar from '../assets/icons/hand-holding-dollar.svg'
import heart from '../assets/icons/heart.svg'
import house from '../assets/icons/house.svg'
import leaf from '../assets/icons/leaf.svg'
import magnifyingGlass from '../assets/icons/magnifying-glass.svg'
import mobileScreenButton from '../assets/icons/mobile-screen-button.svg'
import plus from '../assets/icons/plus.svg'
import screwdriverWrench from '../assets/icons/screwdriver-wrench.svg'
import shirt from '../assets/icons/shirt.svg'
import tabletScreenButton from '../assets/icons/tablet-screen-button.svg'
import tag from '../assets/icons/tag.svg'
import tv from '../assets/icons/tv.svg'
import umbrellaBeach from '../assets/icons/umbrella-beach.svg'
import wallet from '../assets/icons/wallet.svg'

const icons = {
  'bag-shopping': bagShopping,
  'bars': bars,
  'bicycle': bicycle,
  'bolt': bolt,
  'book': book,
  'briefcase': briefcase,
  'camera': camera,
  'car': car,
  'chart-simple': chartSimple,
  'chevron-down': chevronDown,
  'circle-question': circleQuestion,
  'clipboard': clipboard,
  'comment': comment,
  'gamepad': gamepad,
  'gear': gear,
  'gift': gift,
  'hand-holding-dollar': handHoldingDollar,
  'heart': heart,
  'house': house,
  'leaf': leaf,
  'magnifying-glass': magnifyingGlass,
  'mobile-screen-button': mobileScreenButton,
  'plus': plus,
  'screwdriver-wrench': screwdriverWrench,
  'shirt': shirt,
  'tablet-screen-button': tabletScreenButton,
  'tag': tag,
  'tv': tv,
  'umbrella-beach': umbrellaBeach,
  'wallet': wallet,
}
type IconName = keyof typeof icons

export default function Icon({ name }: { name: IconName }) {
  return <span className="icon" aria-hidden="true" style={{ maskImage: `url(${JSON.stringify(icons[name])})` }} />
}
