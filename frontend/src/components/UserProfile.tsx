import Avatar from './Avatar'

export default function UserProfile() {
  return (
    <>
      <div className="profile">
        <Avatar />
        <div>
          <strong>Raúl D.</strong>
          <div className="rating"><span aria-label="5 de 5 estrellas">★★★★★</span> (6)</div>
        </div>
      </div>
      <p className="member-since">En Wallapop desde 2016</p>
    </>
  )
}
