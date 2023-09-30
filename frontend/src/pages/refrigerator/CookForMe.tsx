import { useState, useEffect } from 'react'
import Modal from '../../components/Modal'
import Header from '../../components/Header'
import BottomNavigation from '../../components/BottomNavigation'
import * as S from '../../styles/refrigerator/CookForMe.styled'

const CookForMe = () => {
  const [optionToggle, setOptionToggle] = useState(false)
  const [option, setOption] = useState<string | null>('거리 순')
  const optionHandler = (e: React.MouseEvent) => {
    const target = e.target as HTMLButtonElement
    setOption(target.textContent)
    setOptionToggle(!optionToggle)
  }

  const [modalOpen, setModalOpen] = useState(false)
  const showRequest = () => {
    setModalOpen(true)
  }

  // 다른 화면 클릭 시 토글 닫힘 구현 중
  // const closeOption = () => {
  //   if (optionToggle) {
  //     setOptionToggle(false)
  //   }
  // }
  // document.addEventListener('click', closeOption)

  // 요청하기 클릭시 및 견적서 도착시 버튼 구현 중
  // const [requestToggle, setRequestToggle] = useState(false)
  // const requestHandler = (e: React.MouseEvent) => {
  //   const target = e.target as HTMLButtonElement
  //   setRequestToggle(!requestToggle)
  // }

  const OPTION_MENU_LIST = [
    '거리 순',
    '평점 순',
    '리뷰 많은 순',
    '레시피 많은 순',
  ]

  const CHEF_LIST_EXAMPLE = [
    {
      nickName: '나는 요리사',
      rating: 2.1,
      description: '호랑이 구이 레시피',
      distance: 400,
      reviewCount: 11,
    },
    {
      nickName: '나는 요리사2',
      rating: 0.4,
      description: '코끼리 간장 구이 레시피',
      distance: 100,
      reviewCount: 12,
    },
    {
      nickName: '나는 요리사3',
      rating: 4.7,
      description: '맷비둘기 구이 레시피',
      distance: 300,
      reviewCount: 13,
    },
    {
      nickName: '나는 요리사4',
      rating: 3.5,
      description: '고라니 구이 레시피',
      distance: 180,
      reviewCount: 14,
    },
  ]

  // 필터 정렬
  const sortedChefList = CHEF_LIST_EXAMPLE
  const [chefList, setChefList] = useState(CHEF_LIST_EXAMPLE)

  useEffect(() => {
    if (option === '거리 순') {
      const sortedChefList2 = sortedChefList.sort(
        (a, b) => a.distance - b.distance
      )
      setChefList(sortedChefList2)
    } else if (option === '평점 순') {
      const sortedChefList2 = sortedChefList.sort((a, b) => b.rating - a.rating)
      setChefList(sortedChefList2)
    } else if (option === '리뷰 많은 순') {
      const sortedChefList2 = sortedChefList.sort(
        (a, b) => b.reviewCount - a.reviewCount
      )
      setChefList(sortedChefList2)
    }
  }, [option, sortedChefList])

  return (
    <>
      {modalOpen && <Modal setModalOpen={setModalOpen} modalType="request" />}
      <Header />
      <S.Container>
        <S.Map />
        <S.ChefListContainer>
          <S.Info>
            <S.SubTitle>내 주변 요리사</S.SubTitle>
            <S.SelectBox>
              <S.SelectedBox>
                <S.OptionButton
                  onClick={() => {
                    setOptionToggle(!optionToggle)
                  }}
                >
                  <div>{option}</div>
                  <div>v</div>
                </S.OptionButton>
                <S.OptionList $toggle={optionToggle}>
                  {OPTION_MENU_LIST.map(el => (
                    <S.Option key={el}>
                      <S.OptionButton type="button" onClick={optionHandler}>
                        {el}
                      </S.OptionButton>
                    </S.Option>
                  ))}
                </S.OptionList>
              </S.SelectedBox>
            </S.SelectBox>
          </S.Info>
          <S.CardList>
            {chefList.map(el => (
              <S.Card key={el.nickName}>
                <S.CardInfo className="card-info">
                  <img src="" alt="대표 사진" className="mainImg" />
                  <S.ChefInfo className="chef-info">
                    <S.ChefTopInfo className="top-info">
                      <span className="nickName">{el.nickName}</span>
                      <img src="" alt="평점" className="star" />
                      <span className="rating">{`${el.rating}(${el.reviewCount})`}</span>
                    </S.ChefTopInfo>
                    <S.ChefBottomInfo className="bottom-info">
                      {el.description}
                    </S.ChefBottomInfo>
                  </S.ChefInfo>
                </S.CardInfo>
                <S.ElseInfo>
                  <span>{el.distance}m</span>
                  <S.RequestButton type="button">요청하기</S.RequestButton>
                </S.ElseInfo>
              </S.Card>
            ))}
          </S.CardList>
          <S.ButtonList>
            <S.WritingButton type="button">+ 요청서 작성하기</S.WritingButton>
            <S.WritingButton type="button" onClick={showRequest}>
              요청서 목록
            </S.WritingButton>
          </S.ButtonList>
        </S.ChefListContainer>
      </S.Container>
      <BottomNavigation />
    </>
  )
}

export default CookForMe
